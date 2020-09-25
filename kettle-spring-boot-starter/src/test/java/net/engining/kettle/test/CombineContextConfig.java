package net.engining.kettle.test;

import net.engining.pg.support.core.context.ApplicationContextHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * 通用Context配置
 *
 * @author 陈宝
 * @version 1.0
 * @date 2020/9/25 14:54
 * @since 1.0
 */
@Configuration
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
}
