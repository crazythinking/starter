package net.engining.metrics.config;

import com.alibaba.csp.sentinel.metric.extension.MetricExtension;
import net.engining.metrics.sentinel.SentinelMetrics;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(MetricExtension.class)
@ConditionalOnProperty(prefix = "pg.metrics.sentinel", name = "enabled", matchIfMissing = true)
public class SentinelMetricsContextConfig {

    @Bean
    public SentinelMetrics sentinelMetrics(){
        return new SentinelMetrics();
    }

}
