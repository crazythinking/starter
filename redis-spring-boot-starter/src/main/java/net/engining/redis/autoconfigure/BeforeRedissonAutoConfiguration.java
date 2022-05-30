package net.engining.redis.autoconfigure;

import cn.hutool.core.bean.BeanUtil;
import net.engining.pg.props.CommonProperties;
import net.engining.pg.redis.connection.RedissonConnectionConfiguration;
import net.engining.pg.redis.props.MultiRedissonCommonProperties;
import net.engining.pg.redis.props.RedissonCommonProperties;
import net.engining.pg.redis.utils.RedisUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

@Configuration
@EnableConfigurationProperties({
        CommonProperties.class,
        RedisProperties.class,
        RedissonCommonProperties.class,
        MultiRedissonCommonProperties.class
})
@AutoConfigureBefore(RedissonAutoConfiguration.class)
@ConditionalOnProperty(prefix = "pg.redisson", name = "enabled", matchIfMissing = true)
public class BeforeRedissonAutoConfiguration {

    @Bean
    public RedissonAutoConfigurationCustomizer redissonAutoConfigurationCustomizer(
            RedissonConnectionConfiguration redissonConnectionConfiguration) {
        return configuration -> {
            //用自定义的config覆盖原生的config
            BeanUtil.copyProperties(redissonConnectionConfiguration, configuration);

        };
    }

    @SuppressWarnings("AlibabaMethodTooLong")
    @Bean(name = RedisUtil.REDISSON_CONNECTION_CONFIGURATION)
    public RedissonConnectionConfiguration redissonConnectionConfiguration(CommonProperties commonProperties,
                                                                           RedisProperties redisProperties,
                                                                           RedissonCommonProperties redissonProperties
    ) throws MalformedURLException {
        return RedisUtil.config(commonProperties, redisProperties, redissonProperties);
    }

}
