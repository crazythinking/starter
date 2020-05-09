package net.engining.redis.autoconfigure;

import net.engining.pg.config.RedisConfigUtils;
import net.engining.pg.props.CommonProperties;
import net.engining.redis.autoconfigure.redis.annotation.EnableRedissonCache;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于 Redisson 的 Spring Cache 配置，通过{@link EnableRedissonCache}注入此配置
 *
 * @author Eric Lu
 */
@Configuration
@EnableCaching
public class RedissonCacheContextConfiguration extends CachingConfigurerSupport implements ImportAware {

    private String[] value;

    Logger logger = LoggerFactory.getLogger(RedissonCacheContextConfiguration.class);

    /**
     * 缓存时间
     */
    private long ttl;

    /**
     * 最长空闲时间
     */
    private long maxIdleTime;

    @Autowired
    CommonProperties commonProperties;

    /**
     * 由于RedissonSpringCacheManager没有入口添加CacheKeyPrefix，只能通过keyGenerator控制；
     * 根据类名，方法名，参数组合生成缓存Key；
     * 注意在使用{@link org.springframework.cache.annotation.Cacheable}等缓存注解时，需要显示指定“redissonKeyGenerator”；
     *
     * @return
     */
    @Bean("redissonKeyGenerator")
    @Override
    public KeyGenerator keyGenerator() {
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
     * 需要通过{@link org.springframework.cache.annotation.CacheConfig}指定使用此CacheManager；
     * 另外用于{@link RedissonSpringCacheManager}没有入口设置 cache key prefix,
     * 因此需要在使用{@link org.springframework.cache.annotation.Cacheable}时指定key
     *
     */
    @Bean("redissonCacheManager")
    public CacheManager redissonCacheManager(RedissonClient redissonClient) {
        Map<String, CacheConfig> config = new HashMap<>(8);
        //加载通过EnableRedissonCache注解配置内指定cacheNames的独立配置
        for (String s : value) {
            logger.debug("init redisson spring cache config for cache name: {}", s);
            config.put(s, new CacheConfig(ttl, maxIdleTime));
        }

        RedissonSpringCacheManager cacheManager =  new RedissonSpringCacheManager(redissonClient, config);
        cacheManager.setAllowNullValues(true);

        return cacheManager;
    }


    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        Map<String, Object> enableAttrMap = importMetadata
                .getAnnotationAttributes(EnableRedissonCache.class.getName());
        AnnotationAttributes enableAttrs = AnnotationAttributes.fromMap(enableAttrMap);
        this.value = enableAttrs.getStringArray("value");
        this.maxIdleTime = enableAttrs.getNumber("maxIdleTime");
        this.ttl = enableAttrs.getNumber("ttl");
    }
}
