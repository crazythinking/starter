package net.engining.bustream.config;

import net.engining.gm.config.SyncSchedulingContextConfig;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 *
 * @author : Eric Lu
 * @version :
 * @date : 2020-10-29 14:20
 * @since :
 **/
@Profile("stream.Kafka.bindings.pollinput")
@PropertySource("classpath:application-stream.kafka.bindings.pollinput.yml")
@Import({
        //支持Scheduled pollable receive message
        SyncSchedulingContextConfig.class
})
public class StreamKafkaBindingsPollinputConfig {
}
