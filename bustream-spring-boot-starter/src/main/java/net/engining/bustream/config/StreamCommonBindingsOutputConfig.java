package net.engining.bustream.config;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 *
 * @author : Eric Lu
 * @version :
 * @date : 2020-10-29 14:20
 * @since :
 **/
@Profile("stream.common.bindings.output")
@EnableBinding({
        Source.class
})
@PropertySource("classpath:application-stream.common.bindings.output.yml")
public class StreamCommonBindingsOutputConfig {
}
