package net.engining.debezium.autoconfigure;

import com.google.common.collect.Maps;
import io.debezium.connector.mysql.MySqlConnector;
import io.debezium.data.Envelope;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import io.debezium.relational.history.FileDatabaseHistory;
import net.engining.debezium.event.SourceRecordChangeEventConsumer;
import net.engining.debezium.prop.DebeziumProperties;
import net.engining.gm.config.AsyncExtContextConfig;
import net.engining.gm.config.props.GmCommonProperties;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.storage.FileOffsetBackingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Map;

import static io.debezium.data.Envelope.FieldName.*;
import static java.util.stream.Collectors.toMap;

/**
 * The type Debezium configuration.
 *
 * @author n1
 * @since 2021 /6/1 17:01
 */
@Configuration
@ConditionalOnProperty(prefix = "pg.debezium", name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties({
        GmCommonProperties.class,
        DebeziumProperties.class
})
@Import({
        AsyncExtContextConfig.class
})
public class DebeziumAutoConfiguration {

    @Autowired
    DebeziumProperties debeziumProperties;

    @Autowired
    ApplicationContext applicationContext;

    private Map<String, io.debezium.config.Configuration> debeziumConfigurationMap() {
        Map<String, io.debezium.config.Configuration> configs = Maps.newHashMap();
        Map<String, Map<String, String>> namedProperties = debeziumProperties.getNamedProperties();
        namedProperties.forEach((key, propertyMap) -> {
            io.debezium.config.Configuration.Builder builder = io.debezium.config.Configuration.create();
            //name参数直接统一用map的key,保持逻辑上的一致性
            builder.with("name", key);
            propertyMap.forEach(builder::with);
            configs.put(key, builder.build());
        });

        return configs;
    }

    @Bean
    public Map<String, DebeziumServerBootstrap> debeziumServerBootstrapMap() {
        Map<String, DebeziumServerBootstrap> map = Maps.newHashMap();
        Map<String, io.debezium.config.Configuration> configurationMap = debeziumConfigurationMap();
        configurationMap.forEach((key, configuration) -> {
            DebeziumServerBootstrap debeziumServerBootstrap = new DebeziumServerBootstrap();
            DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine =
                    DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
                            .using(configuration.asProperties())
                            .notifying(new SourceRecordChangeEventConsumer(applicationContext))
                            .build();
            debeziumServerBootstrap.setDebeziumEngine(debeziumEngine);
            map.put(key, debeziumServerBootstrap);

            DefaultListableBeanFactory beanFactory =
                    (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
            beanFactory.registerSingleton(key+"DebeziumServerBootstrap", debeziumServerBootstrap);
        });

        return map;
    }



    /**
     * Debezium mysql 配置. 需要mysql用户具有如下权限：<br>
     * <li>SELECT
     * <li>RELOAD
     * <li>SHOW DATABASES
     * <li>REPLICATION SLAVE
     * <li>REPLICATION CLIENT
     *
     * @return configuration
     */
    //@Bean
    public io.debezium.config.Configuration debeziumMysqlConfig() {
        return io.debezium.config.Configuration.create()
//            连接器的Java类名称
                .with("connector.class", MySqlConnector.class.getName())
//            偏移量持久化，用来容错 默认值
                .with("offset.storage", FileOffsetBackingStore.class.getName())
//                偏移量持久化文件路径 默认/tmp/offsets.dat  如果路径配置不正确可能导致无法存储偏移量 可能会导致重复消费变更
//                如果连接器重新启动，它将使用最后记录的偏移量来知道它应该恢复读取源信息中的哪个位置。
                .with("offset.storage.file.filename", "D:\\idea-workspace\\power-gears\\starter\\debezium-spring-boot-starter\\tmp\\offsets.dat")
//                捕获偏移量的周期
                .with("offset.flush.interval.ms", "1000")
//               连接器的唯一名称
                .with("name", "xxljob-mysql-connector")
//                数据库的hostname
                .with("database.hostname", "ubuntu.wsl")
//                端口
                .with("database.port", "3306")
//                用户名
                .with("database.user", "root")
//                密码
                .with("database.password", "111111")
//                 包含的数据库列表
                .with("database.include.list", "xxljob")
//                是否包含数据库表结构层面的变更，建议使用默认值true
                .with("include.schema.changes", "true")
//                包含的table列表：databaseName.tableName
//                .with("table.include.list", "")
//                需要排除的列名：databaseName.tableName.columnName
//                .with("column.exclude.list", "")
//                mysql.cnf 配置的 server-id;
//                由于MySQL的binlog是MySQL复制机制的一部分，为了读取binlog，MySqlConnector实例必须加入MySQL服务器组，
//                这意味着该服务器ID在构成MySQL服务器组的所有进程中必须是唯一的，并且是1到2的(32-1)次幂之间的任意整数
                .with("database.server.id", "1001")
//                	MySQL 服务器或集群的逻辑名称;连接器在其生成的每个源记录的topic字段中包含此逻辑名称，使应用程序能够识别这些记录的来源
                .with("database.server.name", "xxljob-mysql-cdc")
//                根据项目的实际情况选择Snapshot的初始化类型
                .with("snapshot.mode", "initial")
//                历史变更记录
                .with("database.history", FileDatabaseHistory.class.getName())
//                历史变更记录存储位置，存储DDL
                .with("database.history.file.filename", "D:\\idea-workspace\\power-gears\\starter\\debezium-spring-boot-starter\\tmp\\dbhistory.dat")
                .build();
    }

}
