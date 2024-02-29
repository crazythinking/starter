package net.engining.metrics.support;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import io.micrometer.core.instrument.step.StepRegistryConfig;
import net.engining.metrics.MetricsUtils;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 基于数据存储层的指标注册器，可将监控指标数据定时发布到数据存储层；
 * 常用与只支持推送监控数据的监控系统存储层；
 * 注意：
 * <li>这是一个基于步进窗口的MeterRegistry。步进窗口意味着指标数据在固定的时间间隔内累积，
 *      并在每个窗口结束时将累计的度量值发送到监控系统，并清零；
 * <li>Interval参数同时控制其指标计算的间隔步长和数据发布的间隔周期；
 *
 * @author : Eric Lu
 * @version :
 * @date : 2021-12-07 15:37
 * @since :
 **/
public class StoredStepPushMeterRegistry extends StepMeterRegistry {
    /**
     * 数据存储层
     */
    private final MetricsRepositoriesService repositoriesService;

    public StoredStepPushMeterRegistry(MetricsRepositoriesService repositoriesService, Clock clock,
                                       List<String> acceptNamePrefixes, long interval) {
        super(
                new StepRegistryConfig(){

                    @Override
                    public String prefix() {
                        return "step.meter.storage";
                    }

                    @Override
                    public String get(String key) {
                        return null;
                    }

                    @Override
                    public Duration step() {
                        return Duration.ofSeconds(interval);
                    }
                },
                clock
        );

        this.repositoriesService = repositoriesService;
        //只关注业务系统配置的指标
        for (String prefix : acceptNamePrefixes){
            this.config().meterFilter(MeterFilter.acceptNameStartsWith(prefix));
        }
        this.config().meterFilter(MeterFilter.deny());

    }

    @Override
    protected void publish() {
        MetricsUtils.storing(this, repositoriesService);
    }

    @Override
    protected TimeUnit getBaseTimeUnit() {
        return TimeUnit.SECONDS;
    }
}
