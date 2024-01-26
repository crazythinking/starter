package net.engining.bustream.autoconfigure;

import net.engining.bustream.base.AmqpErrorMessageEventListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.support.NackedAmqpMessageException;
import org.springframework.integration.amqp.support.ReturnedAmqpMessageException;

/**
 * 用于依赖 AMQP 特定的上下文配置
 *
 * @author : Eric Lu
 * @version :
 * @since : 2024-01-26 15:15
 **/
@Configuration
@ConditionalOnClass({
        NackedAmqpMessageException.class,
        ReturnedAmqpMessageException.class
})
public class AmqpDepContextConfig {

    @Bean
    @ConditionalOnMissingBean
    public AmqpErrorMessageEventListener amqpErrorMessageEventListener(){
        return new AmqpErrorMessageEventListener();
    }
}
