package net.engining.datasource.autoconfigure;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Maps;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.engining.pg.db.props.DynamicHikariDataSourceProperties;
import net.engining.pg.support.db.datasource.DynamicRoutingDataSource;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 自动装配可切换的动态DataSource;
 * 指定在DynamicDruidDataSourceAutoConfigure之前装配，而DynamicDruidDataSourceAutoConfigure已经指定了在DruidDataSourceAutoConfigure之前，
 * 因此必然也在DruidDataSourceAutoConfigure之前;
 * <br>
 * 注：只在设置pg.datasource.dynamic.hikari.enabled=true时才触发自动装配
 *
 * @author Eric Lu
 * @create 2019-11-04 16:46
 **/
@Configuration
@ConditionalOnClass(DruidDataSource.class)
@ConditionalOnProperty(prefix = "pg.datasource.dynamic.hikari", name = "enabled", havingValue = "true")
@AutoConfigureBefore(DynamicDruidDataSourceAutoConfigure.class)
@EnableConfigurationProperties({
        DataSourceProperties.class,
        DynamicHikariDataSourceProperties.class
})
public class DynamicDataSourceAutoConfigure {
    /** logger */
    private static final Logger log = LoggerFactory.getLogger(DynamicDataSourceAutoConfigure.class);

    @Autowired
    DataSourceProperties basicProperties;

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public DataSource dataSource(DynamicHikariDataSourceProperties dynamicHikariDataSourceProperties) {
        log.info("starting init dynamic datasource......");
        DynamicRoutingDataSource dynamicRoutingDataSource = new DynamicRoutingDataSource();

        Map<Object, Object> dataSourceMap = Maps.newHashMap();

        // 装配默认DataSource
        HikariConfig defaultCf = dynamicHikariDataSourceProperties.getDefaultCf();
        HikariDataSource defaultDataSource = createDataSource(basicProperties, defaultCf);
        dynamicRoutingDataSource.setDefaultTargetDataSource(defaultDataSource);

        // 装配其他DataSource
        Map<String, HikariConfig> cfMap = dynamicHikariDataSourceProperties.getCfMap();
        if(ValidateUtilExt.isNotNullOrEmpty(cfMap)) {
            cfMap.forEach((s, config) -> {
                dataSourceMap.put(s, createDataSource(basicProperties, config));
            });
        }
        dynamicRoutingDataSource.setTargetDataSources(dataSourceMap);

        dynamicRoutingDataSource.afterPropertiesSet();

        return dynamicRoutingDataSource;
    }

    private HikariDataSource createDataSource(DataSourceProperties basicProperties, HikariConfig config) {

        HikariDataSource dataSource = new HikariDataSource(config);
        if (ValidateUtilExt.isNullOrEmpty(dataSource.getJdbcUrl())){
            dataSource.setJdbcUrl(basicProperties.getUrl());
        }
        if (ValidateUtilExt.isNullOrEmpty(dataSource.getUsername())){
            dataSource.setUsername(basicProperties.getUsername());
        }
        if (ValidateUtilExt.isNullOrEmpty(dataSource.getPassword())){
            dataSource.setPassword(basicProperties.getPassword());
        }
        if (ValidateUtilExt.isNullOrEmpty(dataSource.getDriverClassName())){
            dataSource.setDriverClassName(basicProperties.determineDriverClassName());
        }

        if (ValidateUtilExt.isNotNullOrEmpty(basicProperties.getName())) {
            dataSource.setPoolName(basicProperties.getName());
        }

        return dataSource;
    }
}
