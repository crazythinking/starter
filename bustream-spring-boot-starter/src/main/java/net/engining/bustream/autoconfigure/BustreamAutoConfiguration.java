package net.engining.bustream.autoconfigure;

import net.engining.bustream.config.channels.ChannelInput4KafkaConfig;
import net.engining.bustream.config.channels.ChannelInput4RabbitConfig;
import net.engining.bustream.config.channels.ChannelOutput4KafkaConfig;
import net.engining.bustream.config.channels.ChannelOutput4RabbitConfig;
import net.engining.bustream.config.channels.ChannelPollinput4KafkaConfig;
import net.engining.bustream.config.channels.ChannelPollinput4RabbitConfig;
import net.engining.gm.config.props.BustreamProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.ErrorMessage;

import java.io.IOException;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2020-10-29 17:43
 * @since :
 **/
@Configuration
@EnableConfigurationProperties({
        BustreamProperties.class
})
@Import(value = {
        ChannelInput4RabbitConfig.class,
        ChannelInput4KafkaConfig.class,
        ChannelOutput4RabbitConfig.class,
        ChannelOutput4KafkaConfig.class,
        ChannelPollinput4KafkaConfig.class,
        ChannelPollinput4RabbitConfig.class,

})
@ConditionalOnProperty(prefix = "gm.bustream", name = "enabled", matchIfMissing = true)
public class BustreamAutoConfiguration {
    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BustreamAutoConfiguration.class);

    /**
     * 全局统一的 Stream message Handler: 异常处理
     *
     * @param message 消息处理过程中产生的异常消息
     */
    @StreamListener("errorChannel")
    public void error(Message<?> message) throws IOException {
        ErrorMessage errorMessage = (ErrorMessage) message;
        LOGGER.warn("Handling ERROR: {}", errorMessage.getPayload().getMessage());
    }
}
