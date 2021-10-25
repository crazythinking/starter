package net.engining.gm.autoconfigure;

import net.engining.gm.config.WebContextConfig;
import net.engining.gm.config.WebMvcExtContextConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-10-21 12:24
 * @since :
 **/
@Configuration
@ConditionalOnProperty(prefix = "gm.config.enabled", name = "webmvc", havingValue = "true")
@Import(value = {
        WebContextConfig.class,
        WebMvcExtContextConfig.class
})
public class WebMvcAutoConfiguration {
}
