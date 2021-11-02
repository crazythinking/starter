package net.engining.rocksdb.autoconfigure;

import com.google.common.collect.Maps;
import net.engining.pg.rocksdb.props.BasedOptionsProperties;
import net.engining.pg.rocksdb.props.RocksdbProperties;
import net.engining.pg.storage.RocksdbKeyValueAdapter;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.rocksdb.BlockBasedTableConfig;
import org.rocksdb.CompressionType;
import org.rocksdb.Options;
import org.rocksdb.TableFormatConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    @Bean
    public KeyValueOperations keyValueTemplate(KeyValueAdapter keyValueAdapter) {
        return new KeyValueTemplate(keyValueAdapter);
    }

    @Bean
    public KeyValueAdapter keyValueAdapter(RocksdbProperties properties) {
        if (ValidateUtilExt.isNotNullOrEmpty(properties.getOptions())){
            Map<String, Options> optionsMap = Maps.newHashMap();
            for (String key : properties.getOptions().keySet()) {
                BasedOptionsProperties optionProperties = properties.getOptions().get(key);
                Options options = new Options();
                BlockBasedTableConfig tableFormatConfig = new BlockBasedTableConfig();
                options.setTableFormatConfig(tableFormatConfig);
                tableFormatConfig.setBlockSize(optionProperties.getBlockSize());
                tableFormatConfig.setCacheIndexAndFilterBlocks(optionProperties.isCacheIndexAndFilterBlocks());
                tableFormatConfig.setPinL0FilterAndIndexBlocksInCache(optionProperties.isPinL0FilterAndIndexBlocksInCache());
                options.setBottommostCompressionType(transform(optionProperties.getBottommostCompressionType()));
                options.setBytesPerSync(optionProperties.getBytesPerSync());
                options.setCompressionType(transform(optionProperties.getCompressionType()));
                options.setMaxBackgroundJobs(optionProperties.getMaxBackgroundJobs());
                options.setMaxOpenFiles(optionProperties.getMaxOpenFiles());
                options.setMaxWriteBufferNumber(optionProperties.getMaxWriteBufferNumber());
                options.setWriteBufferSize(optionProperties.getWriteBufferSize());
                options.setCreateIfMissing(optionProperties.isCreateIfMissing());
                options.setLevelCompactionDynamicLevelBytes(optionProperties.isEnableLevelCompactionDynamicLevelBytes());

                optionsMap.put(key, options);
            }
            return new RocksdbKeyValueAdapter(optionsMap, properties.getBaseStoragePath());

        }
        return new RocksdbKeyValueAdapter(null, properties.getBaseStoragePath());
    }
    
    private CompressionType transform(net.engining.pg.rocksdb.props.CompressionType compressionType) {
        switch (compressionType) {
            case NO_COMPRESSION:
                return CompressionType.NO_COMPRESSION;
            case DISABLE_COMPRESSION_OPTION:
                return CompressionType.DISABLE_COMPRESSION_OPTION;
            case LZ4_COMPRESSION:
                return CompressionType.LZ4_COMPRESSION;
            case BZLIB2_COMPRESSION:
                return CompressionType.BZLIB2_COMPRESSION;
            case ZSTD_COMPRESSION:
                return CompressionType.ZSTD_COMPRESSION;
            case ZLIB_COMPRESSION:
                return CompressionType.ZLIB_COMPRESSION;
            case LZ4HC_COMPRESSION:
                return CompressionType.LZ4HC_COMPRESSION;
            case SNAPPY_COMPRESSION:
                return CompressionType.SNAPPY_COMPRESSION;
            case XPRESS_COMPRESSION:
                return CompressionType.XPRESS_COMPRESSION;
            default:
                throw new IllegalStateException("Unexpected value: " + compressionType);
        }
    }

}
