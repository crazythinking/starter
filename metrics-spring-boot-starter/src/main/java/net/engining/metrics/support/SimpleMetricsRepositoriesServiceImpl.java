package net.engining.metrics.support;

import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 指标数据存储层简单例子，只是输出指标数据到日志
 *
 * @author : Eric Lu
 * @version :
 * @date : 2021-12-17 13:58
 * @since :
 **/
public class SimpleMetricsRepositoriesServiceImpl implements MetricsRepositoriesService {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleMetricsRepositoriesServiceImpl.class);

    @Override
    public void batchSave(List<?> entities) {
        entities.forEach(o -> {
            if (o instanceof MeterDto){
                MeterDto meter = (MeterDto) o;
                if (LOGGER.isTraceEnabled()){
                    LOGGER.trace(meter.toString());
                }
            }
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //do nothing
    }

    @Override
    public void initializeMeters(MeterRegistry meterRegistry) {
        //do nothing
    }
}
