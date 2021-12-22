package net.engining.metrics.autoconfigure;

import com.alibaba.csp.sentinel.metric.extension.MetricExtension;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import net.engining.metrics.prop.MetricsRegistryProperties;
import net.engining.metrics.sentinel.SentinelMetrics;
import net.engining.metrics.support.MetricsRepositoriesService;
import net.engining.metrics.support.StoredPushMeterRegistry;
import net.engining.metrics.support.StoredStepMeterRegistry;
import net.engining.metrics.undertow.UndertowMetrics;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.undertow.UndertowWebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-12-15 10:00
 * @since :
 **/
@Configuration
@EnableConfigurationProperties({MetricsRegistryProperties.class})
public class MetricsAutoConfiguration {

    @Bean
    @ConditionalOnClass(MetricExtension.class)
    @ConditionalOnProperty(prefix = "pg.metrics.sentinel", name = "enabled", matchIfMissing = true)
    public SentinelMetrics sentinelMetrics(){
        return new SentinelMetrics();
    }

    @Bean
    @ConditionalOnClass(UndertowWebServer.class)
    @ConditionalOnProperty(prefix = "pg.metrics.undertow", name = "enabled", matchIfMissing = true)
    public UndertowMetrics undertowMetrics(){
        return new UndertowMetrics();
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
