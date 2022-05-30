package net.engining.redis.autoconfigure;

import net.engining.pg.config.RedisContextConfig;
import net.engining.pg.props.CommonProperties;
import net.engining.pg.redis.aop.RedissonLockAopHandler;
import net.engining.pg.redis.aop.RedissonMqPublishAopHandler;
import net.engining.pg.redis.operation.HandlerManagementProxy;
import net.engining.pg.redis.operation.HandlerType;
import net.engining.pg.redis.operation.RedisInjectedBeans;
import net.engining.pg.redis.operation.RedissonBinaryOperation;
import net.engining.pg.redis.operation.RedissonCollectionOperation;
import net.engining.pg.redis.operation.RedissonObjectOperation;
import net.engining.pg.redis.props.MultiRedissonCommonProperties;
import net.engining.pg.redis.props.RedissonCommonProperties;
import net.engining.pg.redis.utils.RedisUtil;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.net.MalformedURLException;
import java.util.List;

/**
 * @author Eric Lu
 */
@Configuration
@AutoConfigureAfter(RedissonAutoConfiguration.class)
@ConditionalOnClass(RedissonCommonProperties.class)
@ConditionalOnProperty(prefix = "pg.redisson", name = "enabled", matchIfMissing = true)
@Import({
        RedisContextConfig.class
})
public class AfterRedissonAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(RedissonLockAopHandler.class)
    public RedissonLockAopHandler lockAop() {
        return new RedissonLockAopHandler();
    }

    @Bean
    @ConditionalOnMissingBean(RedissonMqPublishAopHandler.class)
    public RedissonMqPublishAopHandler mqAop() {
        return new RedissonMqPublishAopHandler();
    }

    @Bean
    public HandlerManagementProxy handlerManagementProxy(List<RedisInjectedBeans> injectedBeansList) {
        return new HandlerManagementProxy(injectedBeansList);
    }

    @Bean
    public List<RedisInjectedBeans> injectedBeansList(RedisProperties redisProperties, CommonProperties commonProperties,
                                                      RedissonCommonProperties redissonCommonProperties,
                                                      MultiRedissonCommonProperties multiRedissonCommonProperties
    ) throws MalformedURLException {
        return RedisUtil.setupRedisInjectedBeans(
                redisProperties, commonProperties, redissonCommonProperties, multiRedissonCommonProperties
        );

    }

    @Bean
    @ConditionalOnMissingBean(RedissonBinaryOperation.class)
    public RedissonBinaryOperation redissonBinary(HandlerManagementProxy handlerManagementProxy) {
        //取默认的RedissonBinaryOperation
        return handlerManagementProxy.getHandler(HandlerType.BINARY);
    }

    @Bean
    @ConditionalOnMissingBean(RedissonObjectOperation.class)
    public RedissonObjectOperation redissonObject(HandlerManagementProxy handlerManagementProxy) {
        //取默认的RedissonObjectOperation
        return handlerManagementProxy.getHandler(HandlerType.OBJECT);
    }

    @Bean
    @ConditionalOnMissingBean(RedissonCollectionOperation.class)
    public RedissonCollectionOperation redissonCollection(HandlerManagementProxy handlerManagementProxy) {
        //取默认的RedissonCollectionOperation
        return handlerManagementProxy.getHandler(HandlerType.COLLECTION);
    }

}
