package net.engining.mongo.autoconfigure.autotest;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * 通用Context配置
 *
 * @author Eric Lu
 */
@Configuration
@EnableMongoRepositories(basePackages = {
        "net.engining.mongo.autoconfigure.autotest.support"
})
public class CombineContextConfig {

}
