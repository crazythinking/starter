package net.engining.minio.autoconfigure.actuator;

import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import java.util.Objects;

/**
 * 健康指标
 *
 * @author Eric Lu
 */
public class MinioHealthIndicator implements HealthIndicator {

    private final MinioClient minioClient;
    private final String bucket;

    public MinioHealthIndicator(MinioClient minioClient, String bucketForHealthIndicator) {
        this.minioClient = Objects.requireNonNull(minioClient);
        this.bucket = bucketForHealthIndicator;
    }

    @Override
    public Health health() {
        Health.Builder builder = Health.unknown();
        if (bucket == null) {
            return builder.build();
        }

        try {
            BucketExistsArgs args = BucketExistsArgs.builder()
                    .bucket(bucket)
                    .build();
            if (minioClient.bucketExists(args)) {
                return builder.up()
                        .withDetail("bucket", bucket)
                        .build();
            } else {
                return builder.down()
                        .withDetail("bucket", bucket)
                        .build();
            }
        } catch (Exception e) {
            return builder.down(e)
                    .withDetail("bucket", bucket)
                    .build();
        }
    }

}
