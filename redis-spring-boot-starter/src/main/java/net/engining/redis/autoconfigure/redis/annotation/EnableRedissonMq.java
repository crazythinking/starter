package net.engining.redis.autoconfigure.redis.annotation;

import net.engining.redis.autoconfigure.RedissonMqContextConfiguration;
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
@Import(RedissonMqContextConfiguration.class)
@Configuration
public @interface EnableRedissonMq {

}
