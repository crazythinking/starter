package net.engining.datasource.autoconfigure;

import net.engining.datasource.autoconfigure.support.DynamicDataSourceEndpoint;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-09-02 11:27
 * @since :
 **/
@Configuration
@ConditionalOnProperty(prefix = "pg.datasource.dynamic.actuator", name = "enabled", havingValue = "true")
@AutoConfigureAfter({
        DynamicDataSourceAutoConfigure.class,
        DynamicDruidDataSourceAutoConfigure.class,
        ShardingJdbcAutoConfiguration.class
})
public class ActuatorAutoConfigure {

    @Bean
    DynamicDataSourceEndpoint dynamicDataSourceEndpoint(){
        return new DynamicDataSourceEndpoint();
    }
}
