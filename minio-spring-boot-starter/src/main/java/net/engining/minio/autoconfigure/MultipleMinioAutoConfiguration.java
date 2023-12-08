package net.engining.minio.autoconfigure;

import com.google.common.collect.Maps;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import net.engining.pg.minio.props.MultipleMinioProperties;
import net.engining.pg.storage.minio.MinioClientFactory;
import net.engining.pg.storage.minio.operators.BucketOperations;
import net.engining.pg.storage.minio.operators.CompositeOperations;
import net.engining.pg.storage.minio.operators.ObjectOperations;
import net.engining.pg.storage.minio.operators.SimpleBucketOperations;
import net.engining.pg.storage.minio.operators.SimpleObjectOperations;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.Map;

/**
 * @author : Eric Lu
 * @version :
 * @since : 2023-12-08 14:56
 **/
@ConditionalOnProperty(prefix = "pg.minio.multiple", name = "enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties({MultipleMinioProperties.class})
@AutoConfigureAfter(MinioAutoConfiguration.class)
public class MultipleMinioAutoConfiguration {

    @SuppressWarnings("DataFlowIssue")
    @Bean
    public CompositeOperations compositeOperations(MultipleMinioProperties properties) {
        Map<String, BucketOperations> bucketOperationsMap = Maps.newHashMap();
        Map<String, ObjectOperations> objectOperationsMap = Maps.newHashMap();

        properties.getProperties().forEach((key, value) -> {
            try {
                MinioClient minioClient = new MinioClientFactory(() -> null, value).getObject();

                bucketOperationsMap.put(key, new SimpleBucketOperations(minioClient, value.getDefaultBucket()));
                objectOperationsMap.put(key, new SimpleObjectOperations(minioClient, value.getDefaultBucket()));
            } catch (MinioException e) {
                throw new RuntimeException(e);
            }

        });

        return new CompositeOperations(bucketOperationsMap, objectOperationsMap);
    }
}
