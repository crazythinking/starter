package net.engining.redis.autoconfigure.autotest.cases;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2020-06-22 11:11
 * @since :
 **/
@Service
@CacheConfig(cacheManager = "redissonCacheManager", keyGenerator = "redissonKeyGenerator")
public class RedissonCacheService {

    @Cacheable("pc111")
    public String cachable(String a){
        return "11111"+a;
    }

    @Cacheable("pc111")
    public String cachable2(String a){
        return "22222"+a;
    }

    @Cacheable("pc222")
    public String cachable3(String a){
        return "333"+a;
    }

}
