package net.engining.groovy.autoconfigure.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.scripting.config.LangNamespaceUtils;

/**
 * 动态语言注册器
 *
 * @author Eric Lu
 */
public class GroovyScriptFactoryPostProcessorRegistrar implements ImportBeanDefinitionRegistrar {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(GroovyScriptFactoryPostProcessorRegistrar.class);

    /**
     * The unique name under which the internally managed {@link GroovyScriptFactoryPostProcessor} is
     * registered in the {@link BeanDefinitionRegistry}.
     */
    private static final String SCRIPT_FACTORY_POST_PROCESSOR_BEAN_NAME =
            "net.engining.groovy.autoconfigure.support.GroovyScriptFactoryPostProcessor";

    public GroovyScriptFactoryPostProcessorRegistrar() {
        LOGGER.debug("Injecting Groovy script factory post processor");
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
    }

    /**
     * Register a {@link GroovyScriptFactoryPostProcessor} bean definition in the supplied
     * {@link BeanDefinitionRegistry} if the {@link GroovyScriptFactoryPostProcessor} hasn't
     * already been registered.
     *
     * @param registry the {@link BeanDefinitionRegistry} to register the script processor with
     * @see LangNamespaceUtils#registerScriptFactoryPostProcessorIfNecessary(BeanDefinitionRegistry)
     */
    private void registerScriptFactoryPostProcessorIfNecessary(BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(SCRIPT_FACTORY_POST_PROCESSOR_BEAN_NAME)) {
            BeanDefinition beanDefinition = new RootBeanDefinition(GroovyScriptFactoryPostProcessor.class);
            registry.registerBeanDefinition(SCRIPT_FACTORY_POST_PROCESSOR_BEAN_NAME, beanDefinition);
        }
    }
}
