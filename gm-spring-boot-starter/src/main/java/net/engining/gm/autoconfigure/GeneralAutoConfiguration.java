package net.engining.gm.autoconfigure;

import net.engining.gm.autoconfigure.prop.GmEnabledProperties;
import net.engining.gm.config.GeneralContextConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-10-21 12:30
 * @since :
 **/
@Configuration
@EnableConfigurationProperties({
        GmEnabledProperties.class
})
@Import(value = {
        GeneralContextConfig.class
})
public class GeneralAutoConfiguration {
}
