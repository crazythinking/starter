package net.engining.minio.autoconfigure;

import io.minio.MinioClient;
import net.engining.pg.minio.props.MinioProperties;
import net.engining.pg.storage.minio.MinioClientFactory;
import net.engining.pg.storage.minio.OkHttpClientProvider;
import net.engining.pg.storage.minio.operators.BucketOperations;
import net.engining.pg.storage.minio.operators.ObjectOperations;
import net.engining.pg.storage.minio.operators.SimpleBucketOperations;
import net.engining.pg.storage.minio.operators.SimpleObjectOperations;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@ConditionalOnProperty(prefix = "pg.minio", name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties({MinioProperties.class})
class MinioAutoConfiguration {

    //@Bean
    //@ConditionalOnMissingBean
    //@ConfigurationPropertiesBinding
    //BucketAndObjectConverter bucketAndObjectConverter() {
    //    return new BucketAndObjectConverter();
    //}

    @Bean
    @ConditionalOnMissingBean
    OkHttpClientProvider okHttpClientProvider(ObjectProvider<OkHttpClient> provider) {
        return provider::getIfAvailable;
    }

    @Bean(name = "minioClient")
    @ConditionalOnMissingBean({MinioClient.class})
    MinioClientFactory minioClientFactory(OkHttpClientProvider httpClientProvider, MinioProperties properties) {
        return new MinioClientFactory(httpClientProvider, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    BucketOperations bucketOperators(MinioClient client, MinioProperties properties) {
        return new SimpleBucketOperations(client, properties.getDefaultBucket());
    }

    @Bean
    @ConditionalOnMissingBean
    ObjectOperations objectOperators(MinioClient client, MinioProperties properties) {
        return new SimpleObjectOperations(client, properties.getDefaultBucket());
    }

}
