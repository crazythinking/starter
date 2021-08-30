package net.engining.datasource.autoconfigure;

import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.querydsl.sql.DB2Templates;
import com.querydsl.sql.H2Templates;
import com.querydsl.sql.MySQLTemplates;
import com.querydsl.sql.OracleTemplates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.spring.SpringConnectionProvider;
import com.querydsl.sql.spring.SpringExceptionTranslator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Provider;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

/**
 * Jdbc4QuerydslContextConfig
 *
 * @author luxue
 */
@Configuration
public class MultipleJdbc4QuerydslContextConfig {

    private com.querydsl.sql.Configuration getConfiguration(SQLTemplates templates) {
        com.querydsl.sql.Configuration configuration = new com.querydsl.sql.Configuration(templates);
        configuration.setExceptionTranslator(new SpringExceptionTranslator());
        return configuration;
    }

    private Map<DbType, com.querydsl.sql.Configuration> querydslConfigurationMap(){
        Map<DbType, com.querydsl.sql.Configuration> map = Maps.newHashMap();
        map.put(DbType.Oracle, getConfiguration(OracleTemplates.builder().build()));
        map.put(DbType.MySQL, getConfiguration(MySQLTemplates.builder().build()));
        map.put(DbType.H2, getConfiguration(H2Templates.builder().build()));
        map.put(DbType.DB2, getConfiguration(DB2Templates.builder().build()));
        return map;
    }

    @Bean
    public Map<String, SQLQueryFactory> sqlQueryFactoryMap(
            @Qualifier("multipleDataSourceTable") Table<String, DbType, DataSource> dataSourceTable
    ) {
        Map<String, SQLQueryFactory> sqlQueryFactoryMap = Maps.newHashMap();
        Map<DbType, com.querydsl.sql.Configuration> querydslConfigurationMap = querydslConfigurationMap();
        for (String key : dataSourceTable.rowKeySet()){
            Map<DbType, DataSource> map = dataSourceTable.row(key);
            for (DbType dbType : map.keySet()){
                Provider<Connection> provider = new SpringConnectionProvider(dataSourceTable.get(key, dbType));
                SQLQueryFactory sqlQueryFactory = new SQLQueryFactory(querydslConfigurationMap.get(dbType), provider);
                sqlQueryFactoryMap.put(key, sqlQueryFactory);
            }
        }
        return sqlQueryFactoryMap;
    }
}
