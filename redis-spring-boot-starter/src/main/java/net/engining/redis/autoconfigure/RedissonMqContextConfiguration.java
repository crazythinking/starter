package net.engining.redis.autoconfigure;

import net.engining.pg.redis.mq.RedissonMqConsumerListener;
import net.engining.redis.autoconfigure.redis.annotation.EnableRedissonMQ;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 基于 Redisson 的 Redis MQ 配置，通过{@link EnableRedissonMQ}注入此配置
 * 
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
