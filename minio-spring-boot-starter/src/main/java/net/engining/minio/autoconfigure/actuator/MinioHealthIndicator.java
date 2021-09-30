package net.engining.minio.autoconfigure.actuator;

import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import java.util.Objects;

/**
 * @author 应卓
 * @since 1.0.0
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

        if (bucket == null) {
            return Health.unknown()
                    .build();
        }

        try {
            BucketExistsArgs args = BucketExistsArgs.builder()
                    .bucket(bucket)
                    .build();
            if (minioClient.bucketExists(args)) {
                return Health.up()
                        .withDetail("bucket", bucket)
                        .build();
            } else {
                return Health.down()
                        .withDetail("bucket", bucket)
                        .build();
            }
        } catch (Exception e) {
            return Health.down(e)
                    .withDetail("bucket", bucket)
                    .build();
        }
    }

}
