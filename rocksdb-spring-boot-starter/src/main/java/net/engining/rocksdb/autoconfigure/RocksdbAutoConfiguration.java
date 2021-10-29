package net.engining.rocksdb.autoconfigure;

import com.google.common.collect.Maps;
import net.engining.pg.rocksdb.props.RocksdbOptionProperties;
import net.engining.pg.rocksdb.props.RocksdbProperties;
import net.engining.pg.storage.RocksDBKeyValueAdapter;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.rocksdb.Options;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.keyvalue.core.KeyValueAdapter;
import org.springframework.data.keyvalue.core.KeyValueOperations;
import org.springframework.data.keyvalue.core.KeyValueTemplate;

import java.util.Map;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-02-22 17:25
 * @since :
 **/
@Configuration
@ConditionalOnProperty(prefix = "pg.rocksdb", name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties({
        RocksdbProperties.class
})
public class RocksdbAutoConfiguration {

    @Autowired
    RocksdbProperties properties;

    @Bean
    public KeyValueOperations keyValueTemplate(KeyValueAdapter keyValueAdapter) {
        return new KeyValueTemplate(keyValueAdapter);
    }

    @Bean
    public KeyValueAdapter keyValueAdapter() {
        if (ValidateUtilExt.isNotNullOrEmpty(properties.getOptions())){
            Map<String, Options> optionsMap = Maps.newHashMap();
            for (String key : properties.getOptions().keySet()) {
                RocksdbOptionProperties optionProperties = properties.getOptions().get(key);
                Options options = new Options();
                options.setCreateIfMissing(optionProperties.isCreateIfMissing());

                optionsMap.put(key, options);
            }
            return new RocksDBKeyValueAdapter(optionsMap, properties.getBaseStoragePath());

        }
        return new RocksDBKeyValueAdapter(null, properties.getBaseStoragePath());
    }

}
