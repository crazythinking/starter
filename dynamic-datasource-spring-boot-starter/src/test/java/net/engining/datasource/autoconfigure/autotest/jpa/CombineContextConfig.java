package net.engining.datasource.autoconfigure.autotest.jpa;

import net.engining.pg.support.core.context.ApplicationContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.sql.DataSource;

/**
 * 通用Context配置
 *
 * @author Eric Lu
 */
@Configuration
@EntityScan(basePackages = {
        "net.engining.datasource.autoconfigure.autotest.jpa.support"
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
