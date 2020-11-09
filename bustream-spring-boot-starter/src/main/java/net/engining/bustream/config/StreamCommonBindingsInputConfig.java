package net.engining.bustream.config;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 *
 * @author : Eric Lu
 * @version :
 * @date : 2020-10-29 14:20
 * @since :
 **/
@Profile("stream.common.bindings.input")
@EnableBinding({
        Sink.class
})
@PropertySource("classpath:application-stream.common.bindings.input.yml")
public class StreamCommonBindingsInputConfig {
}
