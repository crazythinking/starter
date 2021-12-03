package net.engining.bustream.config;

import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 *
 * @author : Eric Lu
 * @version :
 * @date : 2020-10-29 14:20
 * @since :
 **/
@Profile("stream.kafka.bindings.input")
@PropertySource("classpath:application-stream.kafka.bindings.input.yml")
public class StreamKafkaBindingsInputConfig {
}
