package net.engining.metrics.autoconfigure.autotest.support;

import cn.hutool.core.date.SystemClock;
import net.engining.metrics.support.MeterDto;
import net.engining.metrics.support.SimpleMetricsRepositoriesServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-12-17 13:58
 * @since :
 **/
public class MyMetricsRepositoriesServiceImpl extends SimpleMetricsRepositoriesServiceImpl {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(MyMetricsRepositoriesServiceImpl.class);

    @Override
    public void batchSave(List<?> entities) {
        long timestamp = SystemClock.now();
        entities.forEach(o -> {
            if (o instanceof MeterDto){
                MeterDto meter = (MeterDto) o;
                //模拟存储
                LOGGER.warn("{}@{}", timestamp, meter);
            }
        });
    }

}
