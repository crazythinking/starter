package net.engining.datasource.autoconfigure;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.spring.boot.autoconfigure.stat.DruidFilterConfiguration;
import com.alibaba.druid.spring.boot.autoconfigure.stat.DruidSpringAopConfiguration;
import com.alibaba.druid.spring.boot.autoconfigure.stat.DruidStatViewServletConfiguration;
import com.alibaba.druid.spring.boot.autoconfigure.stat.DruidWebStatFilterConfiguration;
import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import net.engining.datasource.autoconfigure.support.DataSourceContextConfig;
import net.engining.datasource.autoconfigure.support.JPAContextConfig;
import net.engining.datasource.autoconfigure.support.MultipleJdbc4QuerydslContextConfig;
import net.engining.datasource.autoconfigure.support.TransactionManagementContextConfig;
import net.engining.datasource.autoconfigure.support.Utils;
import net.engining.gm.config.AsyncExtContextConfig;
import net.engining.gm.config.props.GmCommonProperties;
import net.engining.pg.db.props.DruidDataSourceWrapper;
import net.engining.pg.db.props.DynamicDruidDataSourceProperties;
import net.engining.pg.support.core.exception.ErrorCode;
import net.engining.pg.support.core.exception.ErrorMessageException;
import net.engining.pg.support.db.DbType;
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
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.vendor.Database;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;

/**
 * 自动装配可切换的动态DataSource;
 * 指定在DruidDataSourceAutoConfigure之前装配，而DruidDataSourceAutoConfigure已经指定了在DataSourceAutoConfiguration之前，
 * 因此必然也在DataSourceAutoConfiguration之前;
 * <br>
 * 注：只在设置pg.datasource.dynamic.druid.enabled=true时才触发自动装配
 *
 * @author Eric Lu
 * @date 2019-11-04 16:46
 **/
@Configuration
@ConditionalOnClass(DruidDataSource.class)
@ConditionalOnProperty(prefix = "pg.datasource.dynamic.druid", name = "enabled", havingValue = "true")
@AutoConfigureBefore(DruidDataSourceAutoConfigure.class)
@EnableConfigurationProperties({
        DruidStatProperties.class,
        DataSourceProperties.class,
        DynamicDruidDataSourceProperties.class,
        GmCommonProperties.class,
})
@Import({
        DruidSpringAopConfiguration.class,
        DruidStatViewServletConfiguration.class,
        DruidWebStatFilterConfiguration.class,
        DruidFilterConfiguration.class,
        DataSourceContextConfig.class,
        JPAContextConfig.class,
        MultipleJdbc4QuerydslContextConfig.class,
        TransactionManagementContextConfig.class,
        AsyncExtContextConfig.class
})
public class DynamicDruidDataSourceAutoConfigure {
    /** logger */
    private static final Logger log = LoggerFactory.getLogger(DynamicDruidDataSourceAutoConfigure.class);

    Map<Object, Object> dataSourceMap;

    Table<String, DbType, DataSource> dataSourceTable = HashBasedTable.create();

    @Bean("multipleDataSourceTable")
    public Table<String, DbType, DataSource> getDataSourceTable(){
        return dataSourceTable;
    }

    @Bean("dataSourceMap")
    public Map<Database, DataSource> getDataSourceMap() {
        Map<Database, DataSource> map = Maps.newHashMap();
        for (Object key : dataSourceMap.keySet()){
            DataSource dataSource = (DataSource) dataSourceMap.get(key);
        }
        return map;
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public DataSource dataSource(DynamicDruidDataSourceProperties dynamicDruidDataSourceWrapper) {
        log.info("starting init dynamic datasource......");
        DynamicRoutingDataSource dynamicRoutingDataSource = new DynamicRoutingDataSource();

        Map<Object, Object> dataSourceMap = Maps.newHashMap();

        // 装配默认DataSource
        DruidDataSourceWrapper defaultDs = dynamicDruidDataSourceWrapper.getDefaultDs();
        populateDataSourceTable("default", defaultDs);
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
            populateDataSourceTable(s, druidDataSourceWrapper);
        });
        dynamicRoutingDataSource.setTargetDataSources(dataSourceMap);

        dynamicRoutingDataSource.afterPropertiesSet();

        return dynamicRoutingDataSource;
    }

    private void populateDataSourceTable(String s, DruidDataSourceWrapper druidDataSourceWrapper) {
        String driverClassName = druidDataSourceWrapper.getDriverClassName();
        Utils.populateDataSourceTable(s, druidDataSourceWrapper, driverClassName, dataSourceTable);
    }

}
