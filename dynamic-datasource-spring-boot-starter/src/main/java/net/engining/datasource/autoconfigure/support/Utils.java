package net.engining.datasource.autoconfigure.support;

import cn.hutool.db.dialect.DriverNamePool;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.DB2Templates;
import com.querydsl.sql.H2Templates;
import com.querydsl.sql.MySQLTemplates;
import com.querydsl.sql.OracleTemplates;
import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.spring.SpringConnectionProvider;
import com.querydsl.sql.spring.SpringExceptionTranslator;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.engining.pg.storage.clickhouse.qsql.ClickHouseTemplates;
import net.engining.pg.support.core.exception.ErrorCode;
import net.engining.pg.support.core.exception.ErrorMessageException;
import net.engining.pg.support.db.DbType;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import javax.inject.Provider;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-08-03 17:24
 * @since :
 **/
public class Utils {

    public static final Map<DbType, Configuration> QUERYDSL_CONFIGURATION_MAP = Maps.newHashMap();
    static {
        QUERYDSL_CONFIGURATION_MAP.put(DbType.Oracle, getConfiguration(OracleTemplates.builder().build()));
        QUERYDSL_CONFIGURATION_MAP.put(DbType.MySQL, getConfiguration(MySQLTemplates.builder().build()));
        QUERYDSL_CONFIGURATION_MAP.put(DbType.H2, getConfiguration(H2Templates.builder().build()));
        QUERYDSL_CONFIGURATION_MAP.put(DbType.DB2, getConfiguration(DB2Templates.builder().build()));
        QUERYDSL_CONFIGURATION_MAP.put(DbType.PostgreSQL, getConfiguration(PostgreSQLTemplates.builder().build()));
        QUERYDSL_CONFIGURATION_MAP.put(DbType.ClickHouse, getConfiguration(ClickHouseTemplates.builder().build()));
    }

    /**
     * populate the two keys table of datasource
     *
     * @param s               key of datasource
     * @param dataSource      datasource
     * @param driverClassName DB driver full class name
     * @param dataSourceTable struct of dataSourceTable
     */
    public static void populateDataSourceTable(String s, DataSource dataSource, String driverClassName,
                                                  Table<String, DbType, DataSource> dataSourceTable) {
        dataSourceTable.put(s, getDbType(driverClassName), dataSource);
    }

    public static DbType getDbType(String driverClassName){
        switch (driverClassName) {
            case DriverNamePool.DRIVER_DB2:
                return DbType.DB2;
            case DriverNamePool.DRIVER_H2:
                return DbType.H2;
            case DriverNamePool.DRIVER_MYSQL:
            case DriverNamePool.DRIVER_MYSQL_V6:
                return DbType.MySQL;
            case DriverNamePool.DRIVER_ORACLE:
            case DriverNamePool.DRIVER_ORACLE_OLD:
                return DbType.Oracle;
            case DriverNamePool.DRIVER_CLICK_HOUSE:
                return DbType.ClickHouse;
            default:
                throw new ErrorMessageException(
                        ErrorCode.CheckError,
                        String.format("%s，该类型数据库目前不支持", driverClassName)
                );
        }
    }

    public static HikariDataSource createDataSource(DataSourceProperties basicProperties, HikariConfig config) {

        HikariDataSource dataSource = new HikariDataSource(config);
        if (ValidateUtilExt.isNullOrEmpty(dataSource.getJdbcUrl())) {
            dataSource.setJdbcUrl(basicProperties.getUrl());
        }
        if (ValidateUtilExt.isNullOrEmpty(dataSource.getUsername())) {
            dataSource.setUsername(basicProperties.getUsername());
        }
        if (ValidateUtilExt.isNullOrEmpty(dataSource.getPassword())) {
            dataSource.setPassword(basicProperties.getPassword());
        }
        if (ValidateUtilExt.isNullOrEmpty(dataSource.getDriverClassName())) {
            dataSource.setDriverClassName(basicProperties.determineDriverClassName());
        }
        if (ValidateUtilExt.isNotNullOrEmpty(basicProperties.getName())) {
            dataSource.setPoolName(basicProperties.getName());
        }

        return dataSource;
    }

    public static SQLQueryFactory createSQLQueryFactory(DataSource dataSource,
                                                           com.querydsl.sql.Configuration querydslConfiguration
    ) {
        Provider<Connection> provider = new SpringConnectionProvider(dataSource);
        SQLQueryFactory sqlQueryFactory = new SQLQueryFactory(querydslConfiguration, provider);
        return sqlQueryFactory;
    }

    public static com.querydsl.sql.Configuration getConfiguration(SQLTemplates templates) {
        com.querydsl.sql.Configuration configuration = new com.querydsl.sql.Configuration(templates);
        configuration.setExceptionTranslator(new SpringExceptionTranslator());
        return configuration;
    }

}
