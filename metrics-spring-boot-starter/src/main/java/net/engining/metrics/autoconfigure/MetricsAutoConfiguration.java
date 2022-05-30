package net.engining.metrics.autoconfigure;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import net.engining.metrics.config.DropwizardMetricsContextConfig;
import net.engining.metrics.config.SentinelMetricsContextConfig;
import net.engining.metrics.config.UndertowMetricsContextConfig;
import net.engining.metrics.prop.MetricsRegistryProperties;
import net.engining.metrics.support.MetricsRepositoriesService;
import net.engining.metrics.support.SimpleMetricsRepositoriesServiceImpl;
import net.engining.metrics.support.StoredPushMeterRegistry;
import net.engining.metrics.support.StoredStepMeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-12-15 10:00
 * @since :
 **/
@Configuration
@EnableConfigurationProperties({MetricsRegistryProperties.class})
@Import({
        DropwizardMetricsContextConfig.class,
        SentinelMetricsContextConfig.class,
        UndertowMetricsContextConfig.class
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
