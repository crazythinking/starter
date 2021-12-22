package net.engining.metrics.autoconfigure.autotest;

import net.engining.metrics.autoconfigure.autotest.support.SimpleMetricsRepositoriesServiceImpl;
import net.engining.metrics.support.MetricsRepositoriesService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 通用Context配置
 *
 * @author Eric Lu
 */
@Configuration
public class CombineContextConfig {

    @Bean
    public MetricsRepositoriesService metricsRepositoriesService(){
        return new SimpleMetricsRepositoriesServiceImpl();
    }

}
