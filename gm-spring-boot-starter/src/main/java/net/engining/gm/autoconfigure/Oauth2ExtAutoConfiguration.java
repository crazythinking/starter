package net.engining.gm.autoconfigure;

import net.engining.gm.config.security.OauthResourceServerExtContextConfig;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-10-22 11:05
 * @since :
 **/
@Configuration
@ConditionalOnProperty(prefix = "gm.config.enabled", name = "oauth2", havingValue = "true")
@Import(value = {
        OauthResourceServerExtContextConfig.class
})
@AutoConfigureAfter(OAuth2AutoConfiguration.class)
public class Oauth2ExtAutoConfiguration {

    /**
     * 该Bean需要优先被依赖，需要优先加载，因此通过@Autowired可以达到目的
     */
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    public TokenStore redisTokenStore(RedisConnectionFactory redisConnectionFactory) {
        RedisTokenStore tokenStore = new RedisTokenStore(redisConnectionFactory);
        //TODO 默认是JdkSerialization，应改为使用json，但有坑，暂未解决
        //tokenStore.setSerializationStrategy(new JacksonJsonSerializationStrategy());
        return tokenStore;
    }
}
