package net.engining.datasource.autoconfigure.support;

import com.google.common.base.Strings;
import com.google.common.collect.Table;
import com.querydsl.sql.SQLQueryFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.engining.pg.db.props.DruidDataSourceWrapper;
import net.engining.pg.db.props.DynamicDruidDataSourceProperties;
import net.engining.pg.db.props.DynamicHikariDataSourceProperties;
import net.engining.pg.support.core.context.ApplicationContextHolder;
import net.engining.pg.support.core.exception.ErrorCode;
import net.engining.pg.support.core.exception.ErrorMessageException;
import net.engining.pg.support.db.DbType;
import net.engining.pg.support.db.datasource.DynamicRoutingDataSource;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;

/**
 * 支持动态添加数据源的Actuator端点
 *
 * @author : Eric Lu
 * @version :
 * @date : 2021-09-01 14:31
 * @since :
 **/
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@RestControllerEndpoint(id = "dynamicDataSource")
public class DynamicDataSourceEndpoint {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDataSourceEndpoint.class);
    static final String TRUE = "true";

    @Autowired
    Environment environment;

    @Autowired
    DataSourceProperties basicProperties;

    @Autowired
    DataSource dataSource;

    @Autowired
    @Qualifier("multipleDataSourceTable")
    Table<String, DbType, DataSource> dataSourceTable;

    @Autowired
    @Qualifier("sqlQueryFactoryMap")
    Map<String, SQLQueryFactory> sqlQueryFactoryMap;

    @Autowired
    @Qualifier("dataSourceMap")
    Map<Object, Object> dataSourceMap;

    /**
     * 调用此端点添加数据源时，需要确保key相关联的DataSource配置已添加到ApplicationContext内
     * @param key DataSource配置对应的key
     */
    @PostMapping("/add")
    public void addDataSource(String key) {
        String hikariEnable = environment.getProperty("pg.datasource.dynamic.hikari.enabled");
        String druidEnable = environment.getProperty("pg.datasource.dynamic.druid.enabled");
        String shardingsphereEnable = environment.getProperty("spring.shardingsphere.enabled");

        if (dataSource instanceof DynamicRoutingDataSource){
            if (ValidateUtilExt.isNotNullOrEmpty(hikariEnable) && TRUE.equals(hikariEnable)){
                DynamicHikariDataSourceProperties properties =
                        ApplicationContextHolder.getBean(DynamicHikariDataSourceProperties.class);
                if (ValidateUtilExt.isNotNullOrEmpty(properties.getCfMap().get(key))
                        && ValidateUtilExt.isNullOrEmpty(dataSourceTable.row(key))
                        && ValidateUtilExt.isNullOrEmpty(sqlQueryFactoryMap.get(key))
                ){
                    HikariConfig hikariConfig = properties.getCfMap().get(key);
                    HikariDataSource hikariDataSource = Utils.createDataSource(basicProperties, hikariConfig);
                    Utils.populateDataSourceTable(key, hikariDataSource, hikariConfig.getDriverClassName(), dataSourceTable);
                    sqlQueryFactoryMap.put(
                            key,
                            Utils.createSQLQueryFactory(
                                    hikariDataSource,
                                    Utils.QUERYDSL_CONFIGURATION_MAP.get(
                                            Utils.getDbType(hikariConfig.getDriverClassName())
                                    )
                            )
                    );
                    this.dataSourceMap.put(key, hikariDataSource);
                }
            }
            else if (ValidateUtilExt.isNotNullOrEmpty(druidEnable) && TRUE.equals(druidEnable)){
                DynamicDruidDataSourceProperties properties =
                        ApplicationContextHolder.getBean(DynamicDruidDataSourceProperties.class);
                if (ValidateUtilExt.isNotNullOrEmpty(properties.getDsMap().get(key))
                        && ValidateUtilExt.isNullOrEmpty(dataSourceTable.row(key))
                        && ValidateUtilExt.isNullOrEmpty(sqlQueryFactoryMap.get(key))
                ){
                    DruidDataSourceWrapper dataSourceWrapper = properties.getDsMap().get(key);
                    try {
                        dataSourceWrapper.init();
                        LOGGER.info("init dynamic datasource:{} success......", key);
                    } catch (SQLException e) {
                        throw new ErrorMessageException(
                                ErrorCode.SystemError,
                                Strings.lenientFormat("Dynamic datasource:{}, call init has error", key),
                                e
                        );
                    }
                    Utils.populateDataSourceTable(
                            key,
                            dataSourceWrapper,
                            dataSourceWrapper.getDriverClassName(),
                            dataSourceTable
                    );
                    sqlQueryFactoryMap.put(
                            key,
                            Utils.createSQLQueryFactory(
                                    dataSourceWrapper,
                                    Utils.QUERYDSL_CONFIGURATION_MAP.get(
                                            Utils.getDbType(dataSourceWrapper.getDriverClassName())
                                    )
                            )
                    );
                    this.dataSourceMap.put(key, dataSourceWrapper);
                }
            }
            ((DynamicRoutingDataSource) dataSource).setTargetDataSources(this.dataSourceMap);
            ((DynamicRoutingDataSource) dataSource).afterPropertiesSet();
        }
        else {
            //shardingsphere 本身的实现不支持动态更新的入口，需要找到其DataSource IOC依赖的Beans
            LOGGER.warn("ShardingSphere DataSource 不支持动态添加");
        }

    }

}
