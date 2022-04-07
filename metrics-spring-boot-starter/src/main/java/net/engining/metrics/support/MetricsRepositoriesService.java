package net.engining.metrics.support;

import io.micrometer.core.instrument.MeterRegistry;
import net.engining.pg.storage.core.RepositoriesService;

/**
 * 指标数据的存储层
 *
 * @author Eric Lu
 * @date 2021-12-17 13:49
 **/
public interface MetricsRepositoriesService extends RepositoriesService {

    /**
     * 启动时初始化指标的值，通常从指标数据的存储层获取
     *
     * @param meterRegistry 指标注册器
     */
    void initializeMeters(MeterRegistry meterRegistry);
}
