package net.engining.metrics.autoconfigure;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import net.engining.metrics.config.DebeziumMetricsContextConfig;
import net.engining.metrics.config.DropwizardMetricsContextConfig;
import net.engining.metrics.config.SentinelMetricsContextConfig;
import net.engining.metrics.config.UndertowMetricsContextConfig;
import net.engining.metrics.prop.MetricsRegistryProperties;
import net.engining.metrics.support.MetricsRepositoriesService;
import net.engining.metrics.support.SimpleMetricsRepositoriesServiceImpl;
import net.engining.metrics.support.StoredPushMeterRegistry;
import net.engining.metrics.support.StoredStepMeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.time.Duration;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-12-15 10:00
 * @since :
 **/
@Configuration
@AutoConfigureBefore(CompositeMeterRegistryAutoConfiguration.class)
@EnableConfigurationProperties({MetricsRegistryProperties.class})
@Import({
        DropwizardMetricsContextConfig.class,
        SentinelMetricsContextConfig.class,
        UndertowMetricsContextConfig.class,
        DebeziumMetricsContextConfig.class
})
public class MetricsAutoConfiguration {

    /**
     * 声明指标服务
     * @return 指标服务
     */
    @Bean
    public MetricsRepositoriesService  simpleMetricsRepositoriesService() {
        return new SimpleMetricsRepositoriesServiceImpl();
    }

    /**
     * 增加 gateway metrics 的 Histogram 配置
     */
    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {

        return registry -> {
            registry.config().meterFilter(
                    new MeterFilter() {
                        @Override
                        public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
                            String regex = "^(http"+"){1}.*";
                            if (id.getType() == Meter.Type.TIMER
                                    && id.getName().matches(regex)) {
                                return DistributionStatisticConfig.builder()
                                        .percentilesHistogram(true)
                                        .percentiles(0.5, 0.75, 0.90, 0.95, 0.99)
                                        // 定义了6个桶
                                        .serviceLevelObjectives(
                                                Duration.ofMillis(100).toNanos(),
                                                Duration.ofMillis(500).toNanos(),
                                                Duration.ofSeconds(1).toNanos(),
                                                Duration.ofSeconds(2).toNanos(),
                                                Duration.ofSeconds(3).toNanos(),
                                                Duration.ofSeconds(5).toNanos()
                                        )
                                        .minimumExpectedValue(new Double(Duration.ofMillis(100).toNanos()))
                                        .maximumExpectedValue(new Double(Duration.ofSeconds(5).toNanos()))
                                        .build()
                                        .merge(config);
                            } else {
                                return config;
                            }
                        }
                    });
        };
    }

    /**
     * destroyMethod=""，是由于Spring会自动将MeterRegistry加入CompositeMeterRegistry，其内部会在结束时调用close方法
     */
    @Bean(destroyMethod="")
    @ConditionalOnBean(MetricsRepositoriesService.class)
    public StoredStepMeterRegistry storedStepMeterRegistry(MetricsRepositoriesService repositoriesService,
                                                           MetricsRegistryProperties properties, Clock clock) {
        StoredStepMeterRegistry storedStepMeterRegistry = new StoredStepMeterRegistry(
                repositoriesService,
                clock,
                properties.getStepMeterRegistryPrefixes(),
                properties.getStepMeterRegistryInterval()
        );
        storedStepMeterRegistry.start(new NamedThreadFactory("step-metrics-storage-publisher"));
        return storedStepMeterRegistry;
    }

    /**
     * destroyMethod=""，是由于Spring会自动将MeterRegistry加入CompositeMeterRegistry，其内部会在结束时调用close方法
     */
    @Bean(destroyMethod="")
    @ConditionalOnBean(MetricsRepositoriesService.class)
    public StoredPushMeterRegistry storedPushMeterRegistry(MetricsRepositoriesService repositoriesService,
                                                           MetricsRegistryProperties properties, Clock clock) {
        StoredPushMeterRegistry storedPushMeterRegistry = new StoredPushMeterRegistry(
                repositoriesService,
                clock,
                properties.getPushMeterRegistryPrefixes(),
                properties.getPushMeterRegistryInterval()
        );
        storedPushMeterRegistry.start(new NamedThreadFactory("cumulative-metrics-storage-publisher"));
        return storedPushMeterRegistry;
    }

}
