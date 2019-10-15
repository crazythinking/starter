package net.engining.redis.autoconfigure;

import net.engining.pg.redis.mq.RedissonMqConsumerListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redis MQ配置
 * @author Eric Lu
 */
@Configuration
public class RedissonMqContextConfiguration {

    @Bean
    @ConditionalOnMissingBean(RedissonMqConsumerListener.class)
    public RedissonMqConsumerListener redissonMqListener() {
        return new RedissonMqConsumerListener();
    }
}
