package net.engining.bustream.config;

import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * 此处引入了两个配置的原因是因为KafkaBinderConfigurationProperties实例化是依赖与KafkaProperties的，由AutoConfig机制自动注入；
 *
 * @author : Eric Lu
 * @version :
 * @date : 2020-10-29 14:20
 * @since :
 **/
@Profile("bustream.kafka.binders")
@PropertySource(value ={
        "classpath:application-kafka.yml",
        "classpath:application-bustream.kafka.binders.yml",
})
public class BustreamKafkaBindersConfig {

}
