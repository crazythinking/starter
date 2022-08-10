package net.engining.elasticsearch.autoconfigure.autotest;

import net.engining.pg.support.core.context.ApplicationContextHolder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * 通用Context配置
 *
 * @author Eric Lu
 */
@Configuration
@EnableElasticsearchRepositories({
        "net.engining.elasticsearch.autoconfigure.autotest.support"
})
public class CombineContextConfig {

    /**
     * ApplicationContext的静态辅助Bean，建议项目必须注入
     */
    @Bean
    @Lazy(value=false)
    public ApplicationContextHolder applicationContextHolder(){
        return new ApplicationContextHolder();
    }

}
