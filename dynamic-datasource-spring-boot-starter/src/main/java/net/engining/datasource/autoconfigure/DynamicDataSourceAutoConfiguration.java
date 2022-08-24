package net.engining.datasource.autoconfigure;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.engining.datasource.autoconfigure.support.DataSourceContextConfig;
import net.engining.datasource.autoconfigure.support.JPAContextConfig;
import net.engining.datasource.autoconfigure.support.MultipleJdbc4QuerydslContextConfig;
import net.engining.datasource.autoconfigure.support.TransactionManagementContextConfig;
import net.engining.datasource.autoconfigure.support.Utils;
import net.engining.gm.config.props.GmCommonProperties;
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
import org.springframework.context.annotation.Import;
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
 **/
@Configuration
@ConditionalOnClass(HikariDataSource.class)
@ConditionalOnProperty(prefix = "pg.datasource.dynamic.hikari", name = "enabled", havingValue = "true")
@AutoConfigureBefore(DynamicDruidDataSourceAutoConfiguration.class)
@EnableConfigurationProperties({
        DataSourceProperties.class,
        DynamicHikariDataSourceProperties.class,
        GmCommonProperties.class,
})
@Import({
        DataSourceContextConfig.class,
        JPAContextConfig.class,
        MultipleJdbc4QuerydslContextConfig.class,
        TransactionManagementContextConfig.class
})
public class DynamicDataSourceAutoConfiguration {
    /** logger */
    private static final Logger log = LoggerFactory.getLogger(DynamicDataSourceAutoConfiguration.class);

    @Autowired
    DataSourceProperties basicProperties;

    Table<String, DbType, DataSource> dataSourceTable = HashBasedTable.create();

    @Bean(Utils.MULTIPLE_DATA_SOURCE_TABLE)
    public Table<String, DbType, DataSource> getDataSourceTable(){
        return dataSourceTable;
    }

    Map<Object, Object> dataSourceMap = Maps.newHashMap();

    @Bean(Utils.DATA_SOURCE_MAP)
    public Map<Object, Object> getDataSourceMap() {
        return dataSourceMap;
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public DataSource dataSource(DynamicHikariDataSourceProperties dynamicHikariDataSourceProperties) {
        log.info("starting init dynamic datasource......");
        DynamicRoutingDataSource dynamicRoutingDataSource = new DynamicRoutingDataSource();

        // 装配默认DataSource
        HikariConfig defaultCf = dynamicHikariDataSourceProperties.getDefaultCf();
        HikariDataSource defaultDataSource = Utils.createDataSource(basicProperties, defaultCf);
        populateDataSourceTable("default", defaultCf, defaultDataSource);
        dynamicRoutingDataSource.setDefaultTargetDataSource(defaultDataSource);

        // 装配其他DataSource
        Map<String, HikariConfig> cfMap = dynamicHikariDataSourceProperties.getCfMap();
        if(ValidateUtilExt.isNotNullOrEmpty(cfMap)) {
            cfMap.forEach((s, config) -> {
                HikariDataSource dataSource = Utils.createDataSource(basicProperties, config);
                dataSourceMap.put(s, dataSource);
                populateDataSourceTable(s, config, dataSource);

            });
        }
        dynamicRoutingDataSource.setTargetDataSources(dataSourceMap);
        dynamicRoutingDataSource.afterPropertiesSet();

        return dynamicRoutingDataSource;
    }

    private void populateDataSourceTable(String s, HikariConfig config, HikariDataSource dataSource) {
        Utils.populateDataSourceTable(s, dataSource, config.getDriverClassName(), dataSourceTable);
    }

}
