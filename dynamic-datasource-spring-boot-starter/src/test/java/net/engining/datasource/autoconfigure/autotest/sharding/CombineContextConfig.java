package net.engining.datasource.autoconfigure.autotest.sharding;

import net.engining.pg.support.core.context.ApplicationContextHolder;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * 只使用JPA+QueryDSL存储层
 *
 * @author Eric Lu
 */
@Configuration
@EntityScan(basePackages = {
        "net.engining.datasource.autoconfigure.autotest.sharding.support",
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
