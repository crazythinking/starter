package net.engining.redis.autoconfigure;

import net.engining.pg.redis.props.RedissonCommonProperties;
import net.engining.redis.autoconfigure.redis.annotation.EnableRedisCache;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Cache配置
 *
 * @author Eric Lu
 */
@Configuration
@EnableCaching
@EnableConfigurationProperties(value = RedissonCommonProperties.class)
public class RedissonCacheContextConfiguration implements ImportAware {

    private String[] value;

    Logger logger = LoggerFactory.getLogger(RedissonCacheContextConfiguration.class);

    /**
     * 缓存时间 默认30分钟
     * @return
     */
    private long ttl;

    /**
     * 最长空闲时间 默认30分钟
     * @return
     */
    private long maxIdleTime;

    @Resource
    private RedissonClient redissonClient;

    @Autowired
    private RedissonCommonProperties redissonProperties;

    @Bean
    CacheManager cacheManager() {
        Map<String, CacheConfig> config = new HashMap<>();
        // 创建一个名称为"testMap"的缓存，过期时间ttl为24分钟，同时最长空闲时maxIdleTime为12分钟。
        for (String s : value) {
            logger.info("初始化spring cache空间{}",s);
            config.put(s, new CacheConfig(ttl, maxIdleTime));
        }

        return new RedissonSpringCacheManager(redissonClient, config);
    }


    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        Map<String, Object> enableAttrMap = importMetadata
                .getAnnotationAttributes(EnableRedisCache.class.getName());
        AnnotationAttributes enableAttrs = AnnotationAttributes.fromMap(enableAttrMap);
        this.value=enableAttrs.getStringArray("value");
        this.maxIdleTime=enableAttrs.getNumber("maxIdleTime");
        this.ttl=enableAttrs.getNumber("ttl");
    }
}
