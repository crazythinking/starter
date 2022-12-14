package net.engining.elasticsearch.autoconfigure;

import net.engining.pg.config.ElasticsearchExtContextConfigs;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-11-02 15:42
 * @since :
 **/
@Configuration
@ConditionalOnProperty(prefix = "pg.elasticsearch", name = "enabled", matchIfMissing = true)
@Import(value = {
        ElasticsearchExtContextConfigs.class
})
public class ElasticsearchAutoConfiguration {
}
