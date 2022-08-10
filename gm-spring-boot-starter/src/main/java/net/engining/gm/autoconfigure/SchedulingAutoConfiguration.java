package net.engining.gm.autoconfigure;

import net.engining.gm.config.SyncSchedulingContextConfig;
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
@ConditionalOnProperty(prefix = "gm.config.enabled", name = "scheduling", havingValue = "true")
@Import(value = {
        SyncSchedulingContextConfig.class
})
public class SchedulingAutoConfiguration {
}
