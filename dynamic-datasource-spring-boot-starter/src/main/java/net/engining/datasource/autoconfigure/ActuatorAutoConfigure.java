package net.engining.datasource.autoconfigure;

import com.google.common.collect.Table;
import com.querydsl.sql.SQLQueryFactory;
import net.engining.datasource.autoconfigure.support.DynamicDataSourceEndpoint;
import net.engining.pg.support.db.DbType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-09-02 11:27
 * @since :
 **/
@Configuration
@ConditionalOnProperty(prefix = "pg.datasource.dynamic.actuator", name = "enabled", havingValue = "true")
@AutoConfigureAfter({
        DynamicDataSourceAutoConfigure.class,
        DynamicDruidDataSourceAutoConfigure.class,
        ShardingJdbcAutoConfiguration.class
})
public class ActuatorAutoConfigure {

    @Bean
    DynamicDataSourceEndpoint dynamicDataSourceEndpoint(
            DataSource dataSource,
            @Qualifier("multipleDataSourceTable") Table<String, DbType, DataSource> dataSourceTable,
            @Qualifier("sqlQueryFactoryMap") Map<String, SQLQueryFactory> sqlQueryFactoryMap,
            @Qualifier("dataSourceMap") Map<Object, Object> dataSourceMap
    ){
        return new DynamicDataSourceEndpoint(dataSource, dataSourceTable, sqlQueryFactoryMap, dataSourceMap);
    }
}
