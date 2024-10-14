package net.engining.groovy.autoconfigure.support;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import net.engining.groovy.autoconfigure.annotations.GroovyScriptService;
import net.engining.groovy.autoconfigure.props.GroovyProperties;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.scripting.support.ScriptFactoryPostProcessor;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author : Eric Lu
 * @version :
 * @since : 2024-10-11 14:28
 **/
public class GroovyScriptRegisterApplicationRunner implements ApplicationRunner, Ordered {

    private static final String GROOVY_SCRIPT_FACTORY_CLASS_NAME =
            "org.springframework.scripting.groovy.GroovyScriptFactory";

    private int order = Ordered.HIGHEST_PRECEDENCE;

    private final DefaultListableBeanFactory beanFactory;

    private final Environment environment;

    private final ResourceLoader resourceLoader;

    private final GroovyProperties groovyProperties;

    public GroovyScriptRegisterApplicationRunner(DefaultListableBeanFactory beanFactory, Environment environment,
                                                 ResourceLoader resourceLoader, GroovyProperties groovyProperties
    ){
        this.beanFactory = beanFactory;
        this.environment = environment;
        this.resourceLoader = resourceLoader;
        this.groovyProperties = groovyProperties;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        beanDefinitionRegistering();

    }

    private void beanDefinitionRegistering() {
        // Create script factory bean definition.
        LinkedHashSet<BeanDefinition> beanDefinitions = new LinkedHashSet<>();

        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.setResourceLoader(this.resourceLoader);
        scanner.addIncludeFilter(new AnnotationTypeFilter(GroovyScriptService.class));
        Set<String> basePackages = new HashSet<>(resolveBasePackages());
        for (String basePackage : basePackages) {
            beanDefinitions.addAll(scanner.findCandidateComponents(basePackage));
        }

        for (BeanDefinition definition : beanDefinitions) {
            // verify annotated class is an interface
            if (definition instanceof AnnotatedBeanDefinition) {
                AnnotationMetadata annotationMetadata = ((AnnotatedBeanDefinition) definition).getMetadata();
                Assert.isTrue(annotationMetadata.isInterface(),
                        "@GroovyDynamicLangService can only be specified on an interface");

                Map<String, Object> attributes = annotationMetadata
                        .getAnnotationAttributes(GroovyScriptService.class.getCanonicalName());

                //  don't worry, attributes won't be null
                List<String> scriptSourceLocators = resolveScriptSource(attributes);
                scriptSourceLocators.forEach(scriptSourceLocator -> {
                    BeanDefinition definitionNew = new AnnotatedGenericBeanDefinition(annotationMetadata);
                    definitionNew.setBeanClassName(GROOVY_SCRIPT_FACTORY_CLASS_NAME);
                    definitionNew.setAttribute(
                            ScriptFactoryPostProcessor.LANGUAGE_ATTRIBUTE,
                            GroovyScriptFactoryPostProcessor.GROOVY
                    );

                    long refreshCheckDelay = resolveRefreshCheckDelay(attributes);
                    definitionNew.setAttribute(ScriptFactoryPostProcessor.REFRESH_CHECK_DELAY_ATTRIBUTE, refreshCheckDelay);

                    // Attach jdk proxy target class metadata.!!! 不使用cglib,cglib在刷新后强转会失败
                    // 这也意味着必须使用接口定义，然后由groovy脚本对接口进行不同的实现
                    definitionNew.setAttribute(ScriptFactoryPostProcessor.PROXY_TARGET_CLASS_ATTRIBUTE, false);

                    // Add constructor arguments.
                    ConstructorArgumentValues cav = definitionNew.getConstructorArgumentValues();
                    cav.addIndexedArgumentValue(0, scriptSourceLocator);

                    // beanName, 其实这里会涉及2个bean的产生：
                    // 1. GroovyScriptFactory Bean，这个bean的名字会被父类加上前缀 “scriptFactory.”
                    // 2. 实际的groovy脚本对应的Bean，这个bean的名字就是这里指定的beanName
                    String beanName = GroovyScriptFactoryPostProcessor.beanNameSuffix(scriptSourceLocator);
                    beanFactory.registerBeanDefinition(beanName, definitionNew);
                });

            }
        }
    }

    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (!beanDefinition.getMetadata().isAnnotation()) {
                        isCandidate = true;
                    }
                }
                return isCandidate;
            }
        };
    }

    private List<String> resolveScriptSource(Map<String, Object> attributes) {
        String scriptSource = (String) attributes.get("scriptSources");

        if (ValidateUtilExt.isNullOrEmpty(scriptSource)) {
            throw new IllegalStateException("Must specify either 'scriptSource'");
        }

        //scriptSource 可能是从外部配置项中获取脚本源
        if (scriptSource.startsWith("${") && scriptSource.endsWith("}")) {
            scriptSource = environment.resolveRequiredPlaceholders(scriptSource);
        }

        return StrUtil.split(scriptSource, ",");
    }

    private List<String> resolveBasePackages() {
        Set<String> basePackages = groovyProperties.getBasePackages();
        return Lists.newArrayList(basePackages);
    }

    private long resolveRefreshCheckDelay(Map<String, Object> attributes) {
        String refreshCheckDelay = (String) attributes.get("refreshCheckDelay");

        if (refreshCheckDelay.startsWith("${") && refreshCheckDelay.endsWith("}")) {
            refreshCheckDelay = environment.resolveRequiredPlaceholders(refreshCheckDelay);
        }

        if (!ValidateUtilExt.isNumber(refreshCheckDelay)) {
            throw new IllegalStateException("Must number 'refreshCheckDelay'");
        }

        return Long.parseLong(refreshCheckDelay);
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
