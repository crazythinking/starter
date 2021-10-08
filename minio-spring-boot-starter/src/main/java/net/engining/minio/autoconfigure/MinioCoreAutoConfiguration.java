package net.engining.minio.autoconfigure;

import io.minio.MinioClient;
import net.engining.pg.minio.props.MinioProperties;
import net.engining.pg.storage.minio.BucketAndObjectConverter;
import net.engining.pg.storage.minio.MinioClientFactory;
import net.engining.pg.storage.minio.OkHttpClientProvider;
import net.engining.pg.storage.minio.operators.BucketOperations;
import net.engining.pg.storage.minio.operators.ObjectOperations;
import net.engining.pg.storage.minio.operators.SimpleBucketOperations;
import net.engining.pg.storage.minio.operators.SimpleObjectOperations;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author 应卓
 * @since 1.0.0
 */
@ConditionalOnProperty(prefix = "pg.minio", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(MinioProperties.class)
class MinioCoreAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationPropertiesBinding
    BucketAndObjectConverter bucketAndObjectConverter() {
        return new BucketAndObjectConverter();
    }

    @Bean
    @ConditionalOnMissingBean
    OkHttpClientProvider okHttpClientProvider() {
        return () -> null;
    }

    @Bean(name = "minioClient")
    @ConditionalOnMissingBean({MinioClient.class})
    MinioClientFactory minioClientFactory(OkHttpClientProvider httpClientProvider, MinioProperties properties) {
        return new MinioClientFactory(httpClientProvider, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    BucketOperations bucketOperators(MinioClient client, MinioProperties properties) {
        return new SimpleBucketOperations(client, properties.getBucket());
    }

    @Bean
    @ConditionalOnMissingBean
    ObjectOperations objectOperators(MinioClient client, MinioProperties properties) {
        return new SimpleObjectOperations(client, properties.getBucket());
    }

}
