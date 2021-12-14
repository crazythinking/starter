package net.engining.bustream.config.channels;

import net.engining.bustream.base.stream.StreamPollableInput;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * 针对RabbitMq的 Channel：pollInput 的组合配置
 *
 * @author : Eric Lu
 * @version :
 * @date : 2021-12-03 15:27
 * @since :
 **/
@Configuration
@Profile("channel.stream.pollinput.rabbit")
@EnableBinding({
        StreamPollableInput.class
})
public class ChannelPollinput4RabbitConfig {
}
