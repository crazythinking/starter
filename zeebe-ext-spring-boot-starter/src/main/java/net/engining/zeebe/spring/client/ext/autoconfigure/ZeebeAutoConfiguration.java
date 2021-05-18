package net.engining.zeebe.spring.client.ext.autoconfigure;

import io.camunda.zeebe.spring.client.EnableZeebeClient;
import net.engining.zeebe.spring.client.ext.ZeebeSimpleAdminHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author : Eric Lu
 * @date : 2021-04-16 16:46
 **/
@Configuration
@PropertySource("classpath:application-zeebe.common.properties")
@EnableZeebeClient
public class ZeebeAutoConfiguration {

    @Bean
    public ZeebeSimpleAdminHandler zeebeSimpleAdminHandler(){
        return new ZeebeSimpleAdminHandler();
    }
}
