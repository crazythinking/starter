package net.engining.groovy.autoconfigure;

import net.engining.groovy.autoconfigure.props.GroovyProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2022-11-24 16:34
 * @since :
 **/
@Configuration
@ConditionalOnProperty(prefix = "pg.groovy", name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(GroovyProperties.class)
public class GroovyAutoConfiguration {

}
