package net.engining.metrics.autoconfigure.autotest;

import net.engining.metrics.autoconfigure.autotest.support.MyMetricsRepositoriesServiceImpl;
import net.engining.metrics.support.MetricsRepositoriesService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 通用Context配置
 *
 * @author Eric Lu
 */
@Configuration
public class CombineContextConfig {

    @Bean
    @Primary
    public MetricsRepositoriesService myMetricsRepositoriesService() {
        return new MyMetricsRepositoriesServiceImpl();
    }

}
