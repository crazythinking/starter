package net.engining.debezium.autoconfigure;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import io.debezium.embedded.Connect;
import io.debezium.embedded.EmbeddedEngine;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import io.debezium.relational.HistorizedRelationalDatabaseConnectorConfig;
import io.debezium.relational.history.DatabaseHistory;
import io.debezium.relational.history.FileDatabaseHistory;
import net.engining.debezium.event.SourceRecordChangeEventConsumer;
import net.engining.debezium.facility.DebeziumServerBootstrap;
import net.engining.debezium.facility.JRaftDatabaseHistory;
import net.engining.debezium.facility.JRaftOffsetBackingStore;
import net.engining.debezium.facility.LeaderShiftConnectorCallback;
import net.engining.debezium.prop.DebeziumProperties;
import net.engining.pg.rheakv.props.KvServerProperties;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.apache.kafka.connect.runtime.ConnectorConfig;
import org.apache.kafka.connect.runtime.standalone.StandaloneConfig;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.storage.FileOffsetBackingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * The type Debezium configuration.
 *
 * @author n1
 * @since 2021 /6/1 17:01
 */
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Configuration
@ConditionalOnProperty(prefix = "pg.debezium", name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties({
        DebeziumProperties.class,
        KvServerProperties.class,
})
public class DebeziumAutoConfiguration {

    private static final String OFFSET_FILE_SUFFIX="offsets.dat";
    private static final String HISTORY_FILE_SUFFIX="history.dat";
    private static final String OFFSET_STORAGE_FILE_FILENAME = StandaloneConfig.OFFSET_STORAGE_FILE_FILENAME_CONFIG;
    private static final String DATABASE_HISTORY_FILE_FILENAME = DatabaseHistory.CONFIGURATION_FIELD_PREFIX_STRING + "file.filename";
    private static final String OFFSET_STORAGE = EmbeddedEngine.OFFSET_STORAGE.name();
    private static final String DATABASE_HISTORY = HistorizedRelationalDatabaseConnectorConfig.DATABASE_HISTORY.name();
    public static final String DEBEZIUM_SERVER_BOOTSTRAP_MAP = "debeziumServerBootstrapMap";
    public static final String DATABASE_SERVER_NAME = "database.server.name";

    @Autowired
    DebeziumProperties debeziumProperties;

    @Autowired
    KvServerProperties kvServerProperties;

    @Autowired
    ApplicationContext applicationContext;

    private Map<String, io.debezium.config.Configuration> debeziumConfigurationMap() {
        Map<String, io.debezium.config.Configuration> configs = Maps.newHashMap();
        Map<String, Map<String, String>> namedProperties = debeziumProperties.getNamedProperties();
        namedProperties.forEach((key, propertyMap) -> {
            io.debezium.config.Configuration.Builder builder = io.debezium.config.Configuration.create();
            Map<String, String> properties = namedProperties.get(key);
            //启用JRaft时指定RheaKV作为数据存储
            if (debeziumProperties.isEnabledJRaft()) {
                if (ValidateUtilExt.isNullOrEmpty(properties.get(OFFSET_STORAGE))){
                    builder.with(OFFSET_STORAGE, JRaftOffsetBackingStore.class.getName());
                }
                if (ValidateUtilExt.isNullOrEmpty(properties.get(DATABASE_HISTORY))){
                    builder.with(DATABASE_HISTORY, JRaftDatabaseHistory.class.getName());
                }
            }
            else {
                if (ValidateUtilExt.isNullOrEmpty(properties.get(OFFSET_STORAGE_FILE_FILENAME))){
                    String offsetFileName =
                            debeziumProperties.getDataPath()+ StrUtil.SLASH + key + StrUtil.DASHED + OFFSET_FILE_SUFFIX;
                    builder.with(OFFSET_STORAGE_FILE_FILENAME, offsetFileName);
                }
                if (ValidateUtilExt.isNullOrEmpty(properties.get(DATABASE_HISTORY_FILE_FILENAME))){
                    String histroyFileName =
                            debeziumProperties.getDataPath()+ StrUtil.SLASH + key + StrUtil.DASHED + HISTORY_FILE_SUFFIX;
                    builder.with(DATABASE_HISTORY_FILE_FILENAME, histroyFileName);
                }
                if (ValidateUtilExt.isNullOrEmpty(properties.get(OFFSET_STORAGE))){
                    builder.with(OFFSET_STORAGE, FileOffsetBackingStore.class.getName());
                }
                if (ValidateUtilExt.isNullOrEmpty(properties.get(DATABASE_HISTORY))){
                    builder.with(DATABASE_HISTORY, FileDatabaseHistory.class.getName());
                }
            }

            propertyMap.forEach(builder::with);
            //name参数直接统一用map的key,保持逻辑上的一致性,即使外部配置了也会被覆盖;
            //Connector’s name when registered with the Kafka Connect service.
            //注意其与"database.server.name"的差异，该配置在内部被作为ConnectorId，因此建议保持一致
            builder.with(ConnectorConfig.NAME_CONFIG, key);
            builder.with(DATABASE_SERVER_NAME, key);
            //作为Embedded CDC 不需要数据库全部的DDL，只需要被指定监控表的DDL
            builder.with(DatabaseHistory.STORE_ONLY_CAPTURED_TABLES_DDL.name(), "true");
            configs.put(key, builder.build());
        });

        return configs;
    }

    @Bean(DEBEZIUM_SERVER_BOOTSTRAP_MAP)
    public Map<String, DebeziumServerBootstrap> debeziumServerBootstrapMap() {
        Map<String, DebeziumServerBootstrap> map = Maps.newHashMap();
        Map<String, io.debezium.config.Configuration> configurationMap = debeziumConfigurationMap();
        configurationMap.forEach((key, configuration) -> {
            DebeziumServerBootstrap debeziumServerBootstrap = new DebeziumServerBootstrap(key);
            DebeziumEngine.Builder<RecordChangeEvent<SourceRecord>> builder =
                    DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
                            .using(configuration.asProperties())
                            .notifying(new SourceRecordChangeEventConsumer(applicationContext));
            if (debeziumProperties.isEnabledJRaft()){
                String name = configuration.getString(DATABASE_SERVER_NAME);
                Assert.isTrue(
                        ValidateUtilExt.isNotNullOrEmpty(name),
                        "database.server.name must be set when enabled JRaft"
                );
                //为每个DebeziumServerBootstrap设置一个ConnectorCallback
                kvServerProperties.getStoreOptions().getStoreEngineOptions().getRegionEngineOptionsList().forEach(
                        regionEngineOptions -> {
                            if (regionEngineOptions.getStartKey().equals(name)){
                                builder.using(
                                        new LeaderShiftConnectorCallback(
                                                regionEngineOptions.getRegionId(),
                                                applicationContext
                                        )
                                );
                            }
                        }
                );
            }
            DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine = builder.build();
            debeziumServerBootstrap.setDebeziumEngine(debeziumEngine);
            map.put(key, debeziumServerBootstrap);

            //注册DebeziumEngine到Spring容器
            DefaultListableBeanFactory beanFactory =
                    (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
            beanFactory.registerSingleton(key+"DebeziumServerBootstrap", debeziumServerBootstrap);

            //未开启JRaft时直接启动DebeziumEngine
            if (!debeziumProperties.isEnabledJRaft()) {
                debeziumServerBootstrap.start();
            }
        });

        return map;
    }

}
