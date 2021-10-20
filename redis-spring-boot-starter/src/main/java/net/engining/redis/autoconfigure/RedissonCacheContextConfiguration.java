package net.engining.redis.autoconfigure;

import cn.hutool.core.util.ArrayUtil;
import net.engining.pg.config.RedisCacheContextConfig;
import net.engining.pg.config.support.RedisConfigUtils;
import net.engining.pg.props.CommonProperties;
import net.engining.pg.redis.props.CacheProperties;
import net.engining.pg.redis.props.RedissonCacheProperties;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于 Redisson 的 Spring Cache 配置
 *
 * @author Eric Lu
 */
@SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection"})
@EnableConfigurationProperties({
        CommonProperties.class,
        RedissonCacheProperties.class
})
@Configuration
public class RedissonCacheContextConfiguration extends RedisCacheContextConfig {

    Logger logger = LoggerFactory.getLogger(RedissonCacheContextConfiguration.class);

    @Autowired
    CommonProperties commonProperties;

    @Autowired
    RedissonCacheProperties redissonCacheProperties;

    /**
     * 由于RedissonSpringCacheManager没有入口添加CacheKeyPrefix，只能通过keyGenerator控制；
     * 根据类名，方法名，参数组合生成缓存Key；
     * 注意在使用{@link org.springframework.cache.annotation.Cacheable}等缓存注解时，需要显示指定“redissonKeyGenerator”；
     *
     * @return
     */
    @Bean("redissonKeyGenerator")
    public KeyGenerator redissonKeyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(commonProperties.getAppname());
            sb.append(":");
            return RedisConfigUtils.commonKeyGenerator(target, method, sb, params);
        };

    }

    /**
     * 区别于“cacheManager”，“cacheParameterManager”；
     * 此cacheManager可以对一组cacheNames进行独立的（ttl、maxIdelTime、maxSize）配置；
     * 需要通过{@link org.springframework.cache.annotation.CacheConfig}指定使用此redissonCacheManager；
     * 另外用于{@link RedissonSpringCacheManager}没有入口设置 cache key prefix,
     * 因此需要在使用{@link org.springframework.cache.annotation.CacheConfig}时指定redissonKeyGenerator
     *
     */
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean("redissonCacheManager")
    public CacheManager redissonCacheManager(RedissonClient redissonClient) {
        Map<String, CacheConfig> configMap = new HashMap<>(8);
        List<CacheProperties> cachePropertiesList = redissonCacheProperties.getCachePropertiesList();
        for (CacheProperties cacheProperties : cachePropertiesList) {
            String[] names = cacheProperties.getNames();
            logger.debug("init redisson spring cache config for cache names: {}", ArrayUtil.join(names, ","));
            CacheConfig cacheConfig = new CacheConfig(cacheProperties.getTtl(), cacheProperties.getMaxIdleTime());
            cacheConfig.setMaxSize(cacheProperties.getMaxSize());
            for (String name : names) {
                configMap.put(name, cacheConfig);
            }
        }

        RedissonSpringCacheManager cacheManager =  new RedissonSpringCacheManager(redissonClient, configMap);
        cacheManager.setAllowNullValues(true);

        return cacheManager;
    }

}
