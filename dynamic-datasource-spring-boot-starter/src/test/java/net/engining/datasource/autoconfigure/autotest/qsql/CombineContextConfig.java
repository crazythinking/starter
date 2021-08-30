package net.engining.datasource.autoconfigure.autotest.qsql;

import net.engining.datasource.autoconfigure.autotest.support.OperationLogBizService;
import net.engining.pg.support.core.context.ApplicationContextHolder;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 同时支持JPA+QueryDSL，QueryDSL-SQL 两种存储层
 *
 * @author Eric Lu
 */
@Configuration
@EnableJpaRepositories({
        "net.engining.datasource.autoconfigure.autotest.jpa.support"
})
@EntityScan(basePackages = {
        "net.engining.gm.entity.model"
})
@Import({
        OperationLogBizService.class
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
