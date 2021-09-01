package net.engining.datasource.autoconfigure.autotest.jdbc;

import net.engining.datasource.autoconfigure.autotest.jdbc.support.OperAdtLogExtDto;
import net.engining.gm.config.AsyncExtContextConfig;
import net.engining.gm.config.props.GmCommonProperties;
import net.engining.pg.support.core.context.ApplicationContextHolder;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jdbc.repository.QueryMappingConfiguration;
import org.springframework.data.jdbc.repository.config.DefaultQueryMappingConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 同时支持JPA+QueryDSL，QueryDSL-SQL，JDBC 三种存储层
 *
 * @author Eric Lu
 */
@Configuration
@EnableJdbcRepositories({
        "net.engining.datasource.autoconfigure.autotest.jdbc.support"
})
@EnableJpaRepositories({
        "net.engining.datasource.autoconfigure.autotest.jpa.support"
})
@EntityScan(basePackages = {
        "net.engining.gm.entity.model"
})
@ComponentScan(
        basePackages = {
                "net.engining.datasource.autoconfigure.autotest.support",
                "net.engining.datasource.autoconfigure.autotest.jdbc.support"
        }
)
public class CombineContextConfig {

    /**
     * ApplicationContext的静态辅助Bean，建议项目必须注入
     */
    @Bean
    @Lazy(value=false)
    public ApplicationContextHolder applicationContextHolder(){
        return new ApplicationContextHolder();
    }

    @Bean
    public QueryMappingConfiguration rowMappers() {
        return new DefaultQueryMappingConfiguration()
                .registerRowMapper(OperAdtLogExtDto.class, new OperAdtLogExtDto.OperAdtLogExtDtoRowMapper())
                ;
    }

}
