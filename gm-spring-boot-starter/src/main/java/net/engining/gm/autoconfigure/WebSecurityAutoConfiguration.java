package net.engining.gm.autoconfigure;

import net.engining.gm.config.security.ActuatorWebSecurityExtContextConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-10-22 11:05
 * @since :
 **/
@Configuration
@ConditionalOnProperty(prefix = "gm.config.enabled", name = "security", havingValue = "true")
@Import(value = {
        ActuatorWebSecurityExtContextConfig.class
})
public class WebSecurityAutoConfiguration {

}
