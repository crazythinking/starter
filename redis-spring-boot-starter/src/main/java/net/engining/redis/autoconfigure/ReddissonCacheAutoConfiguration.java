package net.engining.redis.autoconfigure;

import net.engining.pg.redis.props.RedissonCacheProperties;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2020-06-23 9:48
 * @since :
 **/
@Configuration
@EnableConfigurationProperties(value = RedissonCacheProperties.class)
@ConditionalOnClass(RedissonCacheProperties.class)
@ConditionalOnBean(RedissonClient.class)
@ConditionalOnProperty(prefix = "pg.redisson.cache", name = "enabled")
@Import({
        RedissonCacheContextConfiguration.class
})
public class ReddissonCacheAutoConfiguration {
}
