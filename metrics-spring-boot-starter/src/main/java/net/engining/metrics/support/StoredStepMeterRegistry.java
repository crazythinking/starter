package net.engining.metrics.support;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import io.micrometer.core.instrument.step.StepRegistryConfig;
import net.engining.metrics.MetricsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 基于数据存储层的指标注册器，可将监控指标数据发布到数据存储层；
 * 注意：
 * <li>其指标都是按指定步长的累计值；
 * <li>Interval参数同时控制其指标计算的间隔周期与数据发布的间隔周期；
 *
 * @author : Eric Lu
 * @version :
 * @date : 2021-12-07 15:37
 * @since :
 **/
public class StoredStepMeterRegistry extends StepMeterRegistry {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(StoredStepMeterRegistry.class);

    /**
     * 数据存储层
     */
    private final MetricsRepositoriesService repositoriesService;

    public StoredStepMeterRegistry(MetricsRepositoriesService repositoriesService, Clock clock,
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
