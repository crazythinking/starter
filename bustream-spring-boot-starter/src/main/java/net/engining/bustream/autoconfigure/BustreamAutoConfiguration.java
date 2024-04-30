package net.engining.bustream.autoconfigure;

import net.engining.bustream.base.ErrorMessageEvent;
import net.engining.gm.config.props.BustreamProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.ErrorMessage;

import java.util.function.Consumer;

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
        AmqpDepContextConfig.class,
        KafkaDepContextConfig.class

})
@ConditionalOnProperty(prefix = "gm.bustream", name = "enabled", matchIfMissing = true)
public class BustreamAutoConfiguration {

    @Autowired
    private ApplicationEventPublisher publisher;

    /**
     * 全局统一的 Stream message Handler: 异常处理
     *
     */
    @Bean
    public Consumer<ErrorMessage> globalMessageError() {
        return errorMessage -> {
            publisher.publishEvent(new ErrorMessageEvent(errorMessage));
        };
    }

}
