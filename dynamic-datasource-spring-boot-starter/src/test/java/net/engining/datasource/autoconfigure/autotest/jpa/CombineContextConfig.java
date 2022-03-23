package net.engining.datasource.autoconfigure.autotest.jpa;

import net.engining.datasource.autoconfigure.aop.SwitchOrg4HibernateFilterHandler;
import net.engining.pg.support.core.context.ApplicationContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;

/**
 * 只使用JPA+QueryDSL存储层
 *
 * @author Eric Lu
 */
@Configuration
@EnableJpaRepositories({
        "net.engining.datasource.autoconfigure.autotest.jpa.support"
})
@EntityScan(basePackages = {
        "net.engining.datasource.autoconfigure.autotest.jpa.support",
        "net.engining.gm.entity.model"
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
