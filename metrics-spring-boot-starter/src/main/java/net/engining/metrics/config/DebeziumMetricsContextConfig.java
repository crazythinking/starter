package net.engining.metrics.config;

import io.debezium.connector.mysql.MySqlSnapshotChangeEventSourceMetricsMXBean;
import io.debezium.connector.mysql.MySqlStreamingChangeEventSourceMetricsMXBean;
import io.debezium.connector.oracle.OracleStreamingChangeEventSourceMetricsMXBean;
import io.debezium.embedded.EmbeddedEngine;
import io.debezium.pipeline.metrics.SnapshotChangeEventSourceMetricsMXBean;
import io.debezium.relational.history.DatabaseHistoryMXBean;
import net.engining.metrics.debezium.DebeziumMySqlMetrics;
import net.engining.metrics.debezium.DebeziumOracleMetrics;
import net.engining.metrics.prop.DebeziumMetricsProperties;
import net.engining.metrics.prop.MetricsRegistryProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(EmbeddedEngine.class)
@ConditionalOnProperty(prefix = "pg.metrics.debezium", name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties({DebeziumMetricsProperties.class})
public class DebeziumMetricsContextConfig {

    @Bean
    @ConditionalOnClass({
            MySqlStreamingChangeEventSourceMetricsMXBean.class,
            MySqlSnapshotChangeEventSourceMetricsMXBean.class,
            DatabaseHistoryMXBean.class
    })
    public DebeziumMySqlMetrics debeziumMySqlMetrics() {
        return new DebeziumMySqlMetrics();
    }

    @Bean
    @ConditionalOnClass({
            OracleStreamingChangeEventSourceMetricsMXBean.class,
            SnapshotChangeEventSourceMetricsMXBean.class,
            DatabaseHistoryMXBean.class
    })
    public DebeziumOracleMetrics debeziumOracleMetrics() {
        return new DebeziumOracleMetrics();
    }
}
