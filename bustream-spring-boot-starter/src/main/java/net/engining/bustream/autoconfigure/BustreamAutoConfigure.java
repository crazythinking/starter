package net.engining.bustream.autoconfigure;

import net.engining.bustream.config.BusCommonBindingsInputConfig;
import net.engining.bustream.config.BusCommonBindingsOutputConfig;
import net.engining.bustream.config.BusDisableConfig;
import net.engining.bustream.config.BusEnableConfig;
import net.engining.bustream.config.BusKafkaBindingsInputConfig;
import net.engining.bustream.config.BusKafkaBindingsOutputConfig;
import net.engining.bustream.config.BusRabbitBindingsInputConfig;
import net.engining.bustream.config.BusRabbitBindingsOutputConfig;
import net.engining.bustream.config.BustreamKafkaBindersConfig;
import net.engining.bustream.config.BustreamRabbitBindersConfig;
import net.engining.bustream.config.BustreamRocketBindersConfig;
import net.engining.bustream.config.StreamCommonBindingsInputConfig;
import net.engining.bustream.config.StreamCommonBindingsOutputConfig;
import net.engining.bustream.config.StreamCommonBindingsPollinputConfig;
import net.engining.bustream.config.StreamKafkaBindingsInputConfig;
import net.engining.bustream.config.StreamKafkaBindingsOutputConfig;
import net.engining.bustream.config.StreamKafkaBindingsPollinputConfig;
import net.engining.bustream.config.StreamRabbitBindingsInputConfig;
import net.engining.bustream.config.StreamRabbitBindingsOutputConfig;
import net.engining.bustream.config.StreamRabbitBindingsPollinputConfig;
import net.engining.bustream.config.combine.ChannelBusInput4KafkaConfig;
import net.engining.bustream.config.combine.ChannelBusInput4RabbitConfig;
import net.engining.bustream.config.combine.ChannelInput4KafkaConfig;
import net.engining.bustream.config.combine.ChannelInput4RabbitConfig;
import net.engining.bustream.config.combine.ChannelOutput4KafkaConfig;
import net.engining.bustream.config.combine.ChannelOutput4RabbitConfig;
import net.engining.bustream.config.combine.ChannelPollinput4KafkaConfig;
import net.engining.bustream.config.combine.ChannelPollinput4RabbitConfig;
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
        BusDisableConfig.class,
        BusEnableConfig.class,
        BustreamRabbitBindersConfig.class,
        BustreamKafkaBindersConfig.class,
        BustreamRocketBindersConfig.class,
        BusCommonBindingsInputConfig.class,
        BusCommonBindingsOutputConfig.class,
        BusRabbitBindingsInputConfig.class,
        BusKafkaBindingsInputConfig.class,
        BusRabbitBindingsOutputConfig.class,
        BusKafkaBindingsOutputConfig.class,
        StreamCommonBindingsInputConfig.class,
        StreamCommonBindingsOutputConfig.class,
        StreamCommonBindingsPollinputConfig.class,
        StreamRabbitBindingsInputConfig.class,
        StreamKafkaBindingsInputConfig.class,
        StreamRabbitBindingsOutputConfig.class,
        StreamKafkaBindingsOutputConfig.class,
        StreamRabbitBindingsPollinputConfig.class,
        StreamKafkaBindingsPollinputConfig.class,
        ChannelBusInput4KafkaConfig.class,
        ChannelBusInput4RabbitConfig.class,
        ChannelInput4RabbitConfig.class,
        ChannelInput4KafkaConfig.class,
        ChannelOutput4RabbitConfig.class,
        ChannelOutput4KafkaConfig.class,
        ChannelPollinput4RabbitConfig.class,
        ChannelPollinput4KafkaConfig.class,

})
@ConditionalOnProperty(prefix = "gm.bustream", name = "enabled", matchIfMissing = true)
public class BustreamAutoConfigure {
    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BustreamAutoConfigure.class);

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
