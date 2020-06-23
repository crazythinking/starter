package net.engining.redis.autoconfigure.autotest.cases;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2020-06-22 11:42
 * @since :
 **/
@Service
public class RedisCacheService {

    @Cacheable(cacheNames = "origin-pc111")
    public String originCachable(String a){
        return "11111"+a;
    }

}
