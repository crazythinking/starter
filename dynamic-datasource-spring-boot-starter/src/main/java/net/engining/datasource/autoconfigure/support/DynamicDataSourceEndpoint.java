package net.engining.datasource.autoconfigure.support;

import com.google.common.collect.Table;
import com.querydsl.sql.SQLQueryFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.engining.pg.db.props.DynamicHikariDataSourceProperties;
import net.engining.pg.support.core.context.ApplicationContextHolder;
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
import java.util.Map;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-09-01 14:31
 * @since :
 **/
@RestControllerEndpoint(id = "dynamicDataSource")
public class DynamicDataSourceEndpoint {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDataSourceEndpoint.class);
    static final String TRUE = "true";

    @Autowired
    Environment environment;

    @Autowired
    DataSourceProperties basicProperties;

    DataSource dataSource;

    Table<String, DbType, DataSource> dataSourceTable;

    Map<String, SQLQueryFactory> sqlQueryFactoryMap;

    @Autowired
    public DynamicDataSourceEndpoint(DataSource dataSource,
                                     @Qualifier("multipleDataSourceTable") Table<String, DbType, DataSource> dataSourceTable,
                                     Map<String, SQLQueryFactory> sqlQueryFactoryMap
    ) {
        this.dataSource = dataSource;
        this.dataSourceTable = dataSourceTable;
        this.sqlQueryFactoryMap = sqlQueryFactoryMap;
    }

    /**
     * 调用此端点添加数据源时，需要确保key相关联的DataSource配置已添加到ApplicationContext内
     * @param key DataSource配置对应的key
     */
    @PostMapping("/add")
    public void addDataSource(String key){
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
                    HikariDataSource dataSource = Utils.createDataSource(basicProperties, hikariConfig);
                    Utils.populateDataSourceTable(key, dataSource, hikariConfig.getDriverClassName(), dataSourceTable);
                    sqlQueryFactoryMap.put(
                            key,
                            Utils.createSQLQueryFactory(
                                    dataSource,
                                    Utils.QUERYDSL_CONFIGURATION_MAP.get(
                                            Utils.getDbType(hikariConfig.getDriverClassName())
                                    )
                            )
                    );
                }
            }
            else if (ValidateUtilExt.isNotNullOrEmpty(druidEnable) && TRUE.equals(druidEnable)){

            }
        }
        //TODO shardingsphere

    }

}
