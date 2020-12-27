package net.engining.datasource.autoconfigure.autotest;

import net.engining.gm.aop.SpecifiedDataSourceHandler;
import net.engining.pg.support.core.context.ApplicationContextHolder;
import org.aspectj.lang.Aspects;
import org.h2.tools.Server;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.sql.SQLException;

/**
 * 通用Context配置
 *
 * @author Eric Lu
 */
@Configuration
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
@EntityScan(basePackages = {
        "net.engining.datasource.autoconfigure.autotest.cases"
})
public class CombineContextConfig {

    /**
     * ApplicationContext的静态辅助Bean，建议项目必须注入
     * @return
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

    /**
     * h2 tcp server, 方便使用工具访问h2
     * @return
     * @throws SQLException
     */
    @Bean(name="h2tcp", initMethod="start", destroyMethod="stop")
    public Server h2tcp() throws SQLException {

        return Server.createTcpServer("-tcp","-tcpAllowOthers","-tcpPort","49151");

    }

    /**
     * h2 tcp server, 方便使用工具访问h2
     * @return
     * @throws SQLException
     */
    @Bean(name="h2tcpOne", initMethod="start", destroyMethod="stop")
    public Server h2tcpOne() throws SQLException {

        return Server.createTcpServer("-tcp","-tcpAllowOthers","-tcpPort","49152");

    }

}
