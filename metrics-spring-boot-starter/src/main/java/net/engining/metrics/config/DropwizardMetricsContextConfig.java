package net.engining.metrics.config;

import com.codahale.metrics.MetricRegistry;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
@ConditionalOnProperty(
        prefix = "management.metrics.export.prometheus",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class DropwizardMetricsContextConfig {

    @Autowired(required = false)
    List<MetricRegistry> metricRegistries;

    @Autowired
    CollectorRegistry collectorRegistry;

    @PostConstruct
    public void wiredDropwizardMetrics() {
        if(ValidateUtilExt.isNotNullOrEmpty(metricRegistries)){
            metricRegistries.forEach(registry -> {
                collectorRegistry.register(new DropwizardExports(registry));
            });
        }

    }
}
