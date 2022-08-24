package net.engining.redis.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2020-06-23 9:48
 * @since :
 **/
@Configuration
@AutoConfigureAfter(AfterRedissonAutoConfiguration.class)
@ConditionalOnProperty(prefix = "pg.redisson.cache", name = "enabled", havingValue = "true")
@Import({
        RedissonCacheContextConfiguration.class
})
public class RedissonCacheAutoConfiguration {
}
