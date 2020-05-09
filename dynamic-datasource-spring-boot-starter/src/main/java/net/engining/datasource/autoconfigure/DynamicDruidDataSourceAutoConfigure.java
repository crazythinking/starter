package net.engining.datasource.autoconfigure;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.spring.boot.autoconfigure.stat.DruidFilterConfiguration;
import com.alibaba.druid.spring.boot.autoconfigure.stat.DruidSpringAopConfiguration;
import com.alibaba.druid.spring.boot.autoconfigure.stat.DruidStatViewServletConfiguration;
import com.alibaba.druid.spring.boot.autoconfigure.stat.DruidWebStatFilterConfiguration;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import net.engining.datasource.autoconfigure.props.DruidDataSourceWrapper;
import net.engining.datasource.autoconfigure.props.DynamicDruidDataSourceProperties;
import net.engining.pg.support.core.exception.ErrorCode;
import net.engining.pg.support.core.exception.ErrorMessageException;
import net.engining.pg.support.db.datasource.DynamicRoutingDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;

/**
 * 自动装配可切换的动态DataSource;
 * 指定在DruidDataSourceAutoConfigure之前装配，而DruidDataSourceAutoConfigure已经指定了在DataSourceAutoConfiguration之前，
 * 因此必然也在DataSourceAutoConfiguration之前;
 *
 * @author Eric Lu
 * @date 2019-11-04 16:46
 **/
@Configuration
@ConditionalOnClass(DruidDataSource.class)
@ConditionalOnProperty(prefix = "pg.datasource.dynamic.druid", name = "enabled", matchIfMissing = true, havingValue = "true")
@AutoConfigureBefore(DruidDataSourceAutoConfigure.class)
@EnableConfigurationProperties({
        DruidStatProperties.class, DataSourceProperties.class, DynamicDruidDataSourceProperties.class
})
@Import({DruidSpringAopConfiguration.class,
        DruidStatViewServletConfiguration.class,
        DruidWebStatFilterConfiguration.class,
        DruidFilterConfiguration.class})
public class DynamicDruidDataSourceAutoConfigure {
    /** logger */
    private static final Logger log = LoggerFactory.getLogger(DynamicDruidDataSourceAutoConfigure.class);

    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource(DynamicDruidDataSourceProperties dynamicDruidDataSourceWrapper) {
        log.info("starting init dynamic datasource......");
        DynamicRoutingDataSource dynamicRoutingDataSource = new DynamicRoutingDataSource();

        Map<Object, Object> dataSourceMap = Maps.newHashMap();

        // 装配默认DataSource
        DruidDataSourceWrapper defaultDs = dynamicDruidDataSourceWrapper.getDefaultDs();
        try {
            defaultDs.init();
            log.info("init dynamic datasource:default success......");
        } catch (SQLException e) {
            throw new ErrorMessageException(
                    ErrorCode.SystemError,
                    "Dynamic datasource:default, call init has error",
                    e
            );
        }
        dynamicRoutingDataSource.setDefaultTargetDataSource(defaultDs);

        // 装配其他DataSource
        Map<String, DruidDataSourceWrapper> druidDataSourceWrappers = dynamicDruidDataSourceWrapper.getDsMap();
        druidDataSourceWrappers.forEach((s, druidDataSourceWrapper) -> {
            try {
                druidDataSourceWrapper.init();
                log.info("init dynamic datasource:{} success......", s);
            } catch (SQLException e) {
                throw new ErrorMessageException(
                        ErrorCode.SystemError,
                        Strings.lenientFormat("Dynamic datasource:{}, call init has error", s),
                        e
                );
            }
            dataSourceMap.put(s, druidDataSourceWrapper);
        });
        dynamicRoutingDataSource.setTargetDataSources(dataSourceMap);

        dynamicRoutingDataSource.afterPropertiesSet();

        return dynamicRoutingDataSource;
    }

}
