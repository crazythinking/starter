package net.engining.groovy.autoconfigure.autotest;

import net.engining.groovy.autoconfigure.autotest.support.FirstConfig;
import net.engining.groovy.autoconfigure.autotest.support.SecondConfig;
import net.engining.pg.support.core.context.ApplicationContextHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

/**
 * 通用Context配置
 *
 * @author Eric Lu
 */
@Configuration
@Import({
        FirstConfig.class,
        SecondConfig.class
})
public class CombineContextConfig {

    @Bean
    @Lazy(value=false)
    public ApplicationContextHolder applicationContextHolder(){
        return new ApplicationContextHolder();
    }

}
