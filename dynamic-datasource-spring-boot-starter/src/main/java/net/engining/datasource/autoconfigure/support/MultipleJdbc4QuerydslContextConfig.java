package net.engining.datasource.autoconfigure.support;

import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.querydsl.sql.SQLQueryFactory;
import net.engining.pg.support.db.DbType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;

/**
 * QueryDSL-SQL基于JDBC的配置，支持多数据源
 *
 * @author luxue
 */
@Configuration
public class MultipleJdbc4QuerydslContextConfig {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    public Map<String, SQLQueryFactory> sqlQueryFactoryMap(
            @Qualifier("multipleDataSourceTable") Table<String, DbType, DataSource> dataSourceTable
    ) {
        Map<String, SQLQueryFactory> sqlQueryFactoryMap = Maps.newHashMap();
        for (String key : dataSourceTable.rowKeySet()){
            Map<DbType, DataSource> map = dataSourceTable.row(key);
            for (DbType dbType : map.keySet()){
                SQLQueryFactory sqlQueryFactory = Utils.createSQLQueryFactory(
                        dataSourceTable.get(key, dbType),
                        Utils.QUERYDSL_CONFIGURATION_MAP.get(dbType)
                );
                sqlQueryFactoryMap.put(key, sqlQueryFactory);
            }
        }
        return sqlQueryFactoryMap;
    }

}
