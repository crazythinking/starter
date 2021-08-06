package net.engining.datasource.autoconfigure.autotest.qsql;

import net.engining.datasource.autoconfigure.autotest.support.OperationLogService;
import net.engining.pg.support.core.context.ApplicationContextHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

/**
 * 通用Context配置
 *
 * @author Eric Lu
 */
@Configuration
@EnableJdbcRepositories({
        "net.engining.datasource.autoconfigure.autotest.qsql.support"
})
@Import({
        OperationLogService.class
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
