package net.engining.groovy.autoconfigure;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import net.engining.groovy.autoconfigure.props.GroovyProperties;
import net.engining.groovy.autoconfigure.support.GroovyScriptFactoryPostProcessorRegistrar;
import net.engining.groovy.autoconfigure.support.GroovyScriptRegisterApplicationRunner;
import net.engining.groovy.autoconfigure.support.NacosProtocolResolver;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

import javax.annotation.PostConstruct;
import java.util.Properties;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2022-11-24 16:34
 * @since :
 **/
@Configuration
@ConditionalOnProperty(prefix = "pg.groovy", name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(GroovyProperties.class)
@Import({
        GroovyScriptFactoryPostProcessorRegistrar.class
})
public class GroovyAutoConfiguration {

    @Autowired
    private GroovyProperties groovyProperties;

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        if (ValidateUtilExt.isNotNullOrEmpty(groovyProperties.getNacosAddresses())) {
            ((GenericApplicationContext)applicationContext).addProtocolResolver(nacosProtocolResolver());
        }
    }

    @Bean
    public GroovyScriptRegisterApplicationRunner groovyScriptRegisterRunner(DefaultListableBeanFactory beanFactory,
                                                                            Environment environment,
                                                                            ResourceLoader resourceLoader,
                                                                            GroovyProperties groovyProperties) {
        return new GroovyScriptRegisterApplicationRunner(beanFactory, environment, resourceLoader, groovyProperties);
    }

    private NacosProtocolResolver nacosProtocolResolver() {
        ConfigService configService;
        try {
            Properties properties = new Properties();
            properties.put(PropertyKeyConst.SERVER_ADDR, groovyProperties.getNacosAddresses());
            properties.put(PropertyKeyConst.NAMESPACE, groovyProperties.getNacosNamespace());
            configService = NacosFactory.createConfigService(properties);
            return new NacosProtocolResolver(configService);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }

    }

}
