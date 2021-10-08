package net.engining.minio.autoconfigure;

import io.minio.MinioClient;
import net.engining.minio.autoconfigure.actuator.MinioHealthIndicator;
import net.engining.pg.minio.props.MinioProperties;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@ConditionalOnProperty(prefix = "pg.minio", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnClass(name = "org.springframework.boot.actuate.endpoint.annotation.Endpoint")
@AutoConfigureAfter(MinioCoreAutoConfiguration.class)
class MinioActuatorAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    MinioHealthIndicator minioHealthIndicator(MinioClient client, MinioProperties properties) {
        return new MinioHealthIndicator(client, properties.getBucket());
    }

}
