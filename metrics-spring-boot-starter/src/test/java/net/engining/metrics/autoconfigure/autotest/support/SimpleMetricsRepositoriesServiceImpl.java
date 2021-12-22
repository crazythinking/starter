package net.engining.metrics.autoconfigure.autotest.support;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Statistic;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.BaseUnits;
import net.engining.metrics.support.MeterDto;
import net.engining.metrics.support.MetricsRepositoriesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
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
                LOGGER.warn(meter.toString());
            }
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void initializeMeters(MeterRegistry meterRegistry) {
        //load from storage
        double call1 = 20;
        double call2 = 20;
        Counter.builder("biz.mvc.total.call1")
                .tags(
                        "uri", "/mvcecho/111",
                        "type", "cumulative"
                )
                .register(meterRegistry)
                .increment(call1);

        Counter.builder("biz.mvc.total.call2")
                .tags(
                        "uri", "mvcecho3",
                        "type", "cumulative"
                )
                .register(meterRegistry)
                .increment(call2);

    }
}
