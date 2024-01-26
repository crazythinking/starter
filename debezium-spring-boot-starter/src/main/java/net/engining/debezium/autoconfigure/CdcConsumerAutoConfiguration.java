package net.engining.debezium.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.engining.debezium.event.CdcConsumerFuncStreamHandler;
import net.engining.debezium.prop.DebeziumProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CDC 消费者自动配置
 *
 * @author : Eric Lu
 * @version :
 * @since : 2024-01-25 16:37
 **/
@Configuration
@ConditionalOnProperty(prefix = "pg.debezium", name = "enabled-kafka-cdc", havingValue = "true")
@EnableConfigurationProperties({
        DebeziumProperties.class,
})
public class CdcConsumerAutoConfiguration {

    @Bean
    @ConditionalOnBean(ObjectMapper.class)
    public CdcConsumerFuncStreamHandler cdcConsumerFuncStreamHandler(ApplicationContext applicationContext,
                                                                     ObjectMapper objectMapper
    ) {
        return new CdcConsumerFuncStreamHandler(applicationContext, objectMapper);
    }
}
