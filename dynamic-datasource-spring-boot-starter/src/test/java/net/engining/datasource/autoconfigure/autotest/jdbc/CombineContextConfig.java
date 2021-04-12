package net.engining.datasource.autoconfigure.autotest.jdbc;

import net.engining.gm.aop.SpecifiedDataSourceHandler;
import net.engining.pg.support.core.context.ApplicationContextHolder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

/**
 * 通用Context配置
 *
 * @author Eric Lu
 */
@Configuration
@Import(value = {
        JdbcContextConfig.class,
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

    @Bean
    @ConditionalOnMissingBean
    public SpecifiedDataSourceHandler specifiedDataSourceHandler() {
        //使用Spring AOP动态代理方式，使用如下方式
        SpecifiedDataSourceHandler aspect = new SpecifiedDataSourceHandler();
        return aspect;
    }

}
