package net.engining.bustream.config.combine;

import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * 针对Kafka的 Channel：springCloudBusInput 的组合配置
 *
 * @author : Eric Lu
 * @version :
 * @date : 2021-12-03 15:27
 * @since :
 **/
@Profile("channel.bus.input.kafka")
@PropertySource(value = {
        "classpath:application-bus.enable.yml",
        "classpath:application-kafka.yml",
        "classpath:application-bustream.kafka.binders.yml",
        "classpath:application-bus.common.bindings.input.yml",
        "classpath:application-bus.kafka.bindings.input.yml",

})
public class ChannelBusInput4KafkaConfig {
}
