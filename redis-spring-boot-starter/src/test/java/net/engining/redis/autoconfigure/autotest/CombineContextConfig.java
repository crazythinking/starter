package net.engining.redis.autoconfigure.autotest;

import net.engining.metrics.support.MetricsRepositoriesService;
import net.engining.pg.param.props.PgParamAndCacheProperties;
import net.engining.pg.props.CommonProperties;
import net.engining.pg.support.core.context.ApplicationContextHolder;
import net.engining.redis.autoconfigure.autotest.support.MyMetricsRepositoriesServiceImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

/**
 * 通用Context配置
 *
 * @author Eric Lu
 */
@Configuration
@EnableConfigurationProperties(value = {
        CommonProperties.class,
        PgParamAndCacheProperties.class,
})
@EnableCaching
public class CombineContextConfig {

    @Bean
    @Primary
    public MetricsRepositoriesService myMetricsRepositoriesService() {
        return new MyMetricsRepositoriesServiceImpl();
    }

}
