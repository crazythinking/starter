package net.engining.groovy.autoconfigure.support;

import cn.hutool.core.util.StrUtil;
import net.engining.groovy.autoconfigure.annotations.EnableGroovyScript;
import net.engining.groovy.autoconfigure.annotations.GroovyScriptService;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.scripting.config.LangNamespaceUtils;
import org.springframework.scripting.support.ScriptFactoryPostProcessor;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 动态语言注册器
 *
 * @author Eric Lu
 */
@SuppressWarnings("NullableProblems")
public class GroovyScriptRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware, ResourceLoaderAware {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(GroovyScriptRegister.class);

    /**
     * The unique name under which the internally managed {@link GroovyScriptFactoryPostProcessor} is
     * registered in the {@link BeanDefinitionRegistry}.
     */
    private static final String SCRIPT_FACTORY_POST_PROCESSOR_BEAN_NAME =
            "net.engining.groovy.autoconfigure.support.GroovyScriptFactoryPostProcessor";

    private static final String GROOVY_SCRIPT_FACTORY_CLASS_NAME =
            "org.springframework.scripting.groovy.GroovyScriptFactory";

    private Environment environment;

    private ResourceLoader resourceLoader;

    public GroovyScriptRegister() {
        LOGGER.debug("instance");
    }

    /**
     * Implementation of EnvironmentAware interfaces
     */
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * Implementation of ResourceLoaderAware interfaces
     * @param resourceLoader the ResourceLoader object to be used by this object
     */
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * 参考官方实现,添加相关bean定义
     *
     * @param importingClassMetadata import原数据
     * @param registry               bean注册中心
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        // Set up infrastructure.
        registerScriptFactoryPostProcessorIfNecessary(registry);

        // Create script factory bean definition.
        LinkedHashSet<BeanDefinition> beanDefinitions = new LinkedHashSet<>();

        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.setResourceLoader(this.resourceLoader);
        scanner.addIncludeFilter(new AnnotationTypeFilter(GroovyScriptService.class));
        Set<String> basePackages = getBasePackages(importingClassMetadata);
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
                    definitionNew.setAttribute(ScriptFactoryPostProcessor.LANGUAGE_ATTRIBUTE, "groovy");

                    long refreshCheckDelay = resolveRefreshCheckDelay(attributes);
                    definitionNew.setAttribute(ScriptFactoryPostProcessor.REFRESH_CHECK_DELAY_ATTRIBUTE, refreshCheckDelay);

                    // Attach jdk proxy target class metadata.!!! 不使用cglib,cglib在刷新后强转会失败
                    definitionNew.setAttribute(ScriptFactoryPostProcessor.PROXY_TARGET_CLASS_ATTRIBUTE, false);

                    // Add constructor arguments.
                    ConstructorArgumentValues cav = definitionNew.getConstructorArgumentValues();
                    cav.addIndexedArgumentValue(0, scriptSourceLocator);

                    // bean name
                    String beanName = StrUtil.subAfter(scriptSourceLocator, "/", true);

                    registry.registerBeanDefinition(beanName, definitionNew);
                });

            }
        }
    }

    /**
     * Register a {@link GroovyScriptFactoryPostProcessor} bean definition in the supplied
     * {@link BeanDefinitionRegistry} if the {@link GroovyScriptFactoryPostProcessor} hasn't
     * already been registered.
     *
     * @param registry the {@link BeanDefinitionRegistry} to register the script processor with
     * @return the {@link GroovyScriptFactoryPostProcessor} bean definition (new or already registered)
     * @see LangNamespaceUtils#registerScriptFactoryPostProcessorIfNecessary(BeanDefinitionRegistry)
     */
    private BeanDefinition registerScriptFactoryPostProcessorIfNecessary(BeanDefinitionRegistry registry) {
        BeanDefinition beanDefinition;
        if (registry.containsBeanDefinition(SCRIPT_FACTORY_POST_PROCESSOR_BEAN_NAME)) {
            beanDefinition = registry.getBeanDefinition(SCRIPT_FACTORY_POST_PROCESSOR_BEAN_NAME);
        } else {
            beanDefinition = new RootBeanDefinition(GroovyScriptFactoryPostProcessor.class);
            registry.registerBeanDefinition(SCRIPT_FACTORY_POST_PROCESSOR_BEAN_NAME, beanDefinition);
        }
        return beanDefinition;
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

    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata
                .getAnnotationAttributes(EnableGroovyScript.class.getCanonicalName());

        Set<String> basePackages = new HashSet<>();

        //  don't worry, won't be null
        for (String pkg : (String[]) attributes.get("value")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (String pkg : (String[]) attributes.get("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }

        if (basePackages.isEmpty()) {
            basePackages.add(
                    ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }

        //environment.getProperty()

        return basePackages;
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
}
