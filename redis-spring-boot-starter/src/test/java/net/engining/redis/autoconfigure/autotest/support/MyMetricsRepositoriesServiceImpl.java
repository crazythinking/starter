package net.engining.redis.autoconfigure.autotest.support;

import cn.hutool.core.date.SystemClock;
import cn.hutool.extra.spring.SpringUtil;
import io.micrometer.core.instrument.Tag;
import net.engining.metrics.support.MeterDto;
import net.engining.metrics.support.SimpleMetricsRepositoriesServiceImpl;
import net.engining.pg.config.support.RedisConfigUtils;
import net.engining.pg.redis.operation.RedissonObjectOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
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
        long timestamp = SystemClock.now();
        entities.forEach(o -> {
            if (o instanceof MeterDto){
                MeterDto meter = (MeterDto) o;
                StringBuilder sb = new StringBuilder();
                sb.append(meter.getName()).append("{");
                for (int i = 0; i < meter.getTags().size(); i++) {
                    Tag tag = meter.getTags().get(i);
                    sb.append(tag.getKey()).append("=\"").append(tag.getValue()).append("\"");
                    if(i < meter.getTags().size() - 1){
                        sb.append(",");
                    }
                }
                sb.append("}@").append(timestamp);

                redissonObject.setValue(sb.toString(), meter.getMeasurements());
            }
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        redissonObject = SpringUtil.getBean(RedissonObjectOperation.class);
        redisTemplate = SpringUtil.getBean(RedisConfigUtils.REDIS_TEMPLATE);
    }
}
