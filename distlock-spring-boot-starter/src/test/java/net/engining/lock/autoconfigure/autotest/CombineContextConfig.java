package net.engining.lock.autoconfigure.autotest;

import net.engining.lock.autoconfigure.autotest.support.UserService;
import net.engining.pg.support.core.context.ApplicationContextHolder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * 通用Context配置
 *
 * @author Eric Lu
 */
@Configuration
@EnableConfigurationProperties(value = {
})
@EnableCaching
public class CombineContextConfig {

    /**
     * ApplicationContext的静态辅助Bean，建议项目必须注入
     * @return
     */
    @Bean
    @Lazy(value=false)
    public ApplicationContextHolder applicationContextHolder(){
        return new ApplicationContextHolder();
    }

    @Bean
    public UserService userService(){
        return new UserService();
    }

}
