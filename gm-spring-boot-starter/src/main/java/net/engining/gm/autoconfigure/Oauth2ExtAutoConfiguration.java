package net.engining.gm.autoconfigure;

import net.engining.gm.config.security.OauthResourceServerExtContextConfig;
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
@ConditionalOnProperty(prefix = "gm.config.enabled", name = "oauth2", havingValue = "true")
@Import(value = {
        OauthResourceServerExtContextConfig.class
})
public class Oauth2ExtAutoConfiguration {

}
