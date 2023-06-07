package net.engining.mongo.autoconfigure;

import com.mongodb.MongoClientSettings;
import net.engining.mongo.prop.MongoDbProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.autoconfigure.mongo.MongoPropertiesClientSettingsBuilderCustomizer;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.concurrent.TimeUnit;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-11-02 15:42
 * @since :
 **/
@Configuration
@ConditionalOnProperty(prefix = "pg.mongo", name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties({
        MongoDbProperties.class
})
@AutoConfigureBefore({
        MongoAutoConfiguration.class,
        MongoReactiveAutoConfiguration.class
})
public class CustomizedMongoAutoConfiguration {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomizedMongoAutoConfiguration.class);

    @Autowired
    MongoDbProperties mongoDbProperties;

    @Bean
    public MongoClientSettings mongoClientSettings() {
        LOGGER.debug("Customized MongoClientSettings building...");

        return MongoClientSettings.builder()
                .applyToConnectionPoolSettings(builder ->
                        builder.maxSize(mongoDbProperties.getConnectionPool().getMaxSize())
                                .minSize(mongoDbProperties.getConnectionPool().getMinSize())
                                .maxWaitTime(
                                        mongoDbProperties.getConnectionPool().getMaxWaitTime(),
                                        TimeUnit.MILLISECONDS
                                )
                                .maxConnectionLifeTime(
                                        mongoDbProperties.getConnectionPool().getMaxConnectionLifeTime(),
                                        TimeUnit.MILLISECONDS
                                )
                                .maxConnectionIdleTime(
                                        mongoDbProperties.getConnectionPool().getMaxConnectionIdleTime(),
                                        TimeUnit.MILLISECONDS
                                )
                                .maintenanceInitialDelay(
                                        mongoDbProperties.getConnectionPool().getMaintenanceInitialDelay(),
                                        TimeUnit.MILLISECONDS
                                )
                                .maintenanceFrequency(
                                        mongoDbProperties.getConnectionPool().getMaintenanceFrequency(),
                                        TimeUnit.MILLISECONDS
                                )
                )
                .applyToSocketSettings(builder ->
                        builder.connectTimeout(
                                        mongoDbProperties.getSocket().getConnectTimeout(),
                                        TimeUnit.MILLISECONDS
                                )
                                .readTimeout(
                                        mongoDbProperties.getSocket().getReadTimeout(),
                                        TimeUnit.MILLISECONDS
                                )
                )
                .applyToClusterSettings(builder ->
                        builder.serverSelectionTimeout(
                                        mongoDbProperties.getCluster().getServerSelectionTimeout(),
                                        TimeUnit.MILLISECONDS
                                )
                )
                .build();
    }

    @Bean
    MongoPropertiesClientSettingsBuilderCustomizer mongoPropertiesCustomizer(MongoProperties properties,
                                                                             Environment environment) {
        return new MongoPropertiesClientSettingsBuilderCustomizer(properties, environment);
    }
}
