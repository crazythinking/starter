package net.engining.datasource.autoconfigure;

import net.engining.gm.aop.SpecifiedDataSourceHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-08-05 15:38
 * @since :
 **/
@Configuration
public class DataSourceContextConfig {

    @Bean
    @ConditionalOnMissingBean
    public SpecifiedDataSourceHandler specifiedDataSourceHandler() {
        SpecifiedDataSourceHandler aspect = new SpecifiedDataSourceHandler();
        return aspect;
    }

}
