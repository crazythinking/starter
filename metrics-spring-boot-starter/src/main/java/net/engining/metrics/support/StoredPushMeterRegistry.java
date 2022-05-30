package net.engining.metrics.support;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.cumulative.CumulativeCounter;
import io.micrometer.core.instrument.cumulative.CumulativeDistributionSummary;
import io.micrometer.core.instrument.cumulative.CumulativeFunctionCounter;
import io.micrometer.core.instrument.cumulative.CumulativeFunctionTimer;
import io.micrometer.core.instrument.cumulative.CumulativeTimer;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.internal.DefaultGauge;
import io.micrometer.core.instrument.internal.DefaultMeter;
import io.micrometer.core.instrument.push.PushMeterRegistry;
import io.micrometer.core.instrument.push.PushRegistryConfig;
import net.engining.metrics.MetricsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

/**
 * 基于数据存储层的指标注册器，可将监控指标数据发布到数据存储层；
 * 注意：
 * <li>其指标都是累计值，即从初始化那一刻开始一直累计，因此注意重启服务时需要从存储层初始化各指标；
 * <li>Interval参数只控制该指标注册器按周期间隔发布指标数据到存储层的场景；
 *
 * @author : Eric Lu
 * @version :
 * @date : 2021-12-07 15:37
 * @since :
 **/
public class StoredPushMeterRegistry extends PushMeterRegistry implements InitializingBean {
    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StoredPushMeterRegistry.class);

    /**
     * 数据存储层
     */
    private final MetricsRepositoriesService repositoriesService;

    public StoredPushMeterRegistry(MetricsRepositoriesService repositoriesService, Clock clock,
                                   List<String> acceptNamePrefixes, long interval) {
        super(
                new PushRegistryConfig() {

                    @Override
                    public String prefix() {
                        return "cumulative.meter.storage";
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
        for (String prefix : acceptNamePrefixes) {
            this.config().meterFilter(MeterFilter.acceptNameStartsWith(prefix));
        }
        this.config().meterFilter(MeterFilter.deny());

    }

    @Override
    protected void publish() {
        MetricsUtils.storing(this, repositoriesService);
    }

    @Override
    protected <T> Gauge newGauge(Meter.Id id, T obj, ToDoubleFunction<T> valueFunction) {
        return new DefaultGauge<>(id, obj, valueFunction);
    }

    @Override
    protected Counter newCounter(Meter.Id id) {
        return new CumulativeCounter(id);
    }

    @Override
    protected Timer newTimer(Meter.Id id,
                             DistributionStatisticConfig distributionStatisticConfig, PauseDetector pauseDetector) {
        return new CumulativeTimer(
                id,
                clock,
                distributionStatisticConfig,
                pauseDetector,
                this.getBaseTimeUnit()
        );
    }

    @Override
    protected DistributionSummary newDistributionSummary(Meter.Id id,
                                                         DistributionStatisticConfig distributionStatisticConfig,
                                                         double scale) {
        return new CumulativeDistributionSummary(
                id,
                clock,
                distributionStatisticConfig,
                scale,
                false
        );
    }

    @Override
    protected Meter newMeter(Meter.Id id, Meter.Type type, Iterable<Measurement> measurements) {
        return new DefaultMeter(id, type, measurements);
    }

    @Override
    protected <T> FunctionTimer newFunctionTimer(Meter.Id id, T obj, ToLongFunction<T> countFunction,
                                                 ToDoubleFunction<T> totalTimeFunction, TimeUnit totalTimeFunctionUnit) {
        return new CumulativeFunctionTimer<>(
                id,
                obj,
                countFunction,
                totalTimeFunction,
                totalTimeFunctionUnit,
                this.getBaseTimeUnit()
        );
    }

    @Override
    protected <T> FunctionCounter newFunctionCounter(Meter.Id id, T obj, ToDoubleFunction<T> countFunction) {
        return new CumulativeFunctionCounter<>(id, obj, countFunction);
    }

    @Override
    protected TimeUnit getBaseTimeUnit() {
        return TimeUnit.SECONDS;
    }

    @Override
    protected DistributionStatisticConfig defaultHistogramConfig() {
        return DistributionStatisticConfig.builder()
                .build()
                .merge(DistributionStatisticConfig.DEFAULT);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        repositoriesService.initializeMeters(this);
    }
}
