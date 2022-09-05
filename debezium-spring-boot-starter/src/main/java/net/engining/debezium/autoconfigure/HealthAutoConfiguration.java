package net.engining.debezium.autoconfigure;

import net.engining.debezium.autoconfigure.actuator.DebeziumCompositeHealthContributor;
import net.engining.metrics.prop.DebeziumMetricsProperties;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2022-09-05 12:49
 * @since :
 **/
@Configuration
@ConditionalOnProperty(prefix = "management.endpoint.health", name = "enabled", havingValue = "true")
@AutoConfigureAfter(JRaftNodeAutoConfiguration.class)
@EnableConfigurationProperties({
        DebeziumMetricsProperties.class
})
public class HealthAutoConfiguration {

    @Bean
    public DebeziumCompositeHealthContributor debeziumCompositeHealthContributor(DebeziumMetricsProperties properties) {
        return new DebeziumCompositeHealthContributor(properties);
    }
}
