package net.engining.redis.autoconfigure.redis.annotation;

import net.engining.redis.autoconfigure.RedissonCacheContextConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author Eric Lu
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.TYPE })
@Documented
@Import(RedissonCacheContextConfiguration.class)
@Configuration
public @interface EnableRedisCache {

    /**
     * 缓存的名称 @Cacheable,@CachePut,@CacheEvict的value必须包含在这里面
     * @return
     */
    String[] value();

    /**
     * 缓存时间 默认30分钟
     * @return
     */
    long ttl() default 1000*60*30L;

    /**
     * 最长空闲时间 默认30分钟
     * @return
     */
    long maxIdleTime() default 1000*60*30L;
}
