package net.engining.metrics.support;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import net.engining.pg.storage.core.RepositoriesService;

import java.util.List;

/**
 * 指标数据的存储层
 *
 * @author Eric Lu
 * @date 2021-12-17 13:49
 **/
public interface MetricsRepositoriesService extends RepositoriesService {

    void initializeMeters(MeterRegistry meterRegistry);
}
