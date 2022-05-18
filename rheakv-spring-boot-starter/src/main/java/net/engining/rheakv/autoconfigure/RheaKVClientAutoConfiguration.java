package net.engining.rheakv.autoconfigure;

import net.engining.pg.rheakv.props.KvClientProperties;
import net.engining.pg.storage.rheakv.KvClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@ConditionalOnProperty(prefix = "pg.rheakv.client", name = "enabled", havingValue = "true")
@EnableConfigurationProperties({
        KvClientProperties.class,
})
public class RheaKVClientAutoConfiguration {

    @Bean
    public KvClient kvClient(KvClientProperties kvClientProperties, Environment environment) {
        KvClient kvClient = new KvClient(kvClientProperties, environment);
        kvClient.start();
        return kvClient;
    }

}
