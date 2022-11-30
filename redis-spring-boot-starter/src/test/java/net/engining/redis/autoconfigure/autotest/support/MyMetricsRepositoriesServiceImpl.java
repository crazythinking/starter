package net.engining.redis.autoconfigure.autotest.support;

import cn.hutool.extra.spring.SpringUtil;
import io.micrometer.core.instrument.MeterRegistry;
import net.engining.metrics.support.SimpleMetricsRepositoriesServiceImpl;
import net.engining.pg.config.support.RedisConfigUtils;
import net.engining.pg.redis.operation.RedissonObjectOperation;
import net.engining.pg.redis.utils.RedisUtil;
import net.engining.pg.support.core.context.ApplicationContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.List;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-12-17 13:58
 * @since :
 **/
public class MyMetricsRepositoriesServiceImpl extends SimpleMetricsRepositoriesServiceImpl implements InitializingBean {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(MyMetricsRepositoriesServiceImpl.class);

    RedissonObjectOperation redissonObject;

    RedisTemplate<String, Serializable> redisTemplate;

    @Override
    public void batchSave(List<?> entities) {
    }

    @Override
    public void initializeMeters(MeterRegistry meterRegistry) {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        redissonObject = SpringUtil.getBean(RedissonObjectOperation.class);
        redisTemplate = SpringUtil.getBean(RedisConfigUtils.REDIS_TEMPLATE);
    }
}
