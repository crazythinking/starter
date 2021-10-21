package net.engining.debezium.autoconfigure;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import io.debezium.relational.history.FileDatabaseHistory;
import net.engining.debezium.event.SourceRecordChangeEventConsumer;
import net.engining.debezium.prop.DebeziumProperties;
import net.engining.gm.config.AsyncExtContextConfig;
import net.engining.gm.config.props.GmCommonProperties;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.storage.FileOffsetBackingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

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
        GmCommonProperties.class,
        DebeziumProperties.class
})
public class DebeziumAutoConfiguration {

    private static final String OFFSET_FILE_SUFFIX="offsets.dat";
    private static final String HISTORY_FILE_SUFFIX="history.dat";
    private static final String OFFSET_STORAGE_FILE_FILENAME = "offset.storage.file.filename";
    private static final String DATABASE_HISTORY_FILE_FILENAME = "database.history.file.filename";
    private static final String OFFSET_STORAGE = "offset.storage";
    private static final String DATABASE_HISTORY = "database.history";

    @Autowired
    DebeziumProperties debeziumProperties;

    @Autowired
    ApplicationContext applicationContext;

    private Map<String, io.debezium.config.Configuration> debeziumConfigurationMap() {
        Map<String, io.debezium.config.Configuration> configs = Maps.newHashMap();
        Map<String, Map<String, String>> namedProperties = debeziumProperties.getNamedProperties();
        namedProperties.forEach((key, propertyMap) -> {
            io.debezium.config.Configuration.Builder builder = io.debezium.config.Configuration.create();
            Map<String, String> properties = namedProperties.get(key);
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
            propertyMap.forEach(builder::with);
            //name参数直接统一用map的key,保持逻辑上的一致性,即使外部配置了也会被覆盖
            builder.with("name", key);
            configs.put(key, builder.build());
        });

        return configs;
    }

    @Bean
    public Map<String, DebeziumServerBootstrap> debeziumServerBootstrapMap() {
        Map<String, DebeziumServerBootstrap> map = Maps.newHashMap();
        Map<String, io.debezium.config.Configuration> configurationMap = debeziumConfigurationMap();
        configurationMap.forEach((key, configuration) -> {
            DebeziumServerBootstrap debeziumServerBootstrap = new DebeziumServerBootstrap(key);
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

}
