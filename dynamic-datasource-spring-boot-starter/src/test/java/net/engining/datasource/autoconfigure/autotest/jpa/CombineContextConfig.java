package net.engining.datasource.autoconfigure.autotest.jpa;

import net.engining.gm.aop.SpecifiedDataSourceHandler;
import net.engining.gm.config.JPAContextConfig;
import net.engining.gm.config.Jdbc4QuerydslContextConfig;
import net.engining.pg.support.core.context.ApplicationContextHolder;
import org.aspectj.lang.Aspects;
import org.h2.tools.Server;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import java.sql.SQLException;

/**
 * 通用Context配置
 *
 * @author Eric Lu
 */
@Configuration
@Import(value = {
        JPAContextConfig.class,
        Jdbc4QuerydslContextConfig.class
})
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

    @Bean
    @ConditionalOnMissingBean
    public SpecifiedDataSourceHandler specifiedDataSourceHandler() {
        //使用Spring AOP动态代理方式，使用如下方式
        //SpecifiedDataSourceHandler aspect = new SpecifiedDataSourceHandler();

        SpecifiedDataSourceHandler aspect = Aspects.aspectOf(SpecifiedDataSourceHandler.class);
        return aspect;
    }

    /**
     * h2 tcp server, 方便使用工具访问h2；
     * 模拟第二个H2实例
     */
    @Bean(name="h2tcpOne", initMethod="start", destroyMethod="stop")
    public Server h2tcpOne() throws SQLException {

        return Server.createTcpServer("-tcp","-tcpAllowOthers","-tcpPort","49152");

    }

}
