package net.engining.metrics.config;

import io.undertow.Undertow;
import net.engining.metrics.undertow.UndertowMetrics;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(Undertow.class)
@ConditionalOnProperty(prefix = "pg.metrics.undertow", name = "enabled", matchIfMissing = true)
public class UndertowMetricsContextConfig {

    @Bean
    public UndertowMetrics undertowMetrics(){
        return new UndertowMetrics();
    }
}
