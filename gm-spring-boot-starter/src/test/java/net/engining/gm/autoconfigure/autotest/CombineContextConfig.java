package net.engining.gm.autoconfigure.autotest;

import net.engining.gm.autoconfigure.autotest.support.Bean2Inter;
import net.engining.gm.autoconfigure.autotest.support.BeanInter;
import net.engining.gm.autoconfigure.autotest.support.Inter;
import net.engining.pg.support.core.context.ApplicationContextHolder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * 通用Context配置
 *
 * @author Eric Lu
 */
@Configuration
public class CombineContextConfig {

    @Bean
    public BeanInter inter(){
        return new BeanInter();
    }

    @Bean
    public Inter inter2(){
        return new Bean2Inter();
    }
}
