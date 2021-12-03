package net.engining.bustream.config.combine;

import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * 针对RabbitMq的 Channel：output 的组合配置
 *
 * @author : Eric Lu
 * @version :
 * @date : 2021-12-03 15:27
 * @since :
 **/
@Profile("channel.stream.output.rabbit")
@PropertySource(value = {
        "classpath:application-bustream.rabbit.binders.yml",
        "classpath:application-stream.common.bindings.output.yml",
        "classpath:application-stream.rabbit.bindings.output.yml",

})
public class ChannelOutput4RabbitConfig {
}
