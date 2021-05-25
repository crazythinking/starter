package net.engining.zeebe.spring.client.ext.autoconfigure;

import io.camunda.zeebe.spring.client.EnableZeebeClient;
import net.engining.zeebe.spring.client.ext.ZeebeSimpleAdminHandler;
import net.engining.zeebe.spring.client.ext.config.ZeebeConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author : Eric Lu
 * @date : 2021-04-16 16:46
 **/
@Configuration
@Import(value = {
        ZeebeConfig.class
})
@EnableZeebeClient
public class ZeebeAutoConfiguration {

    @Bean
    public ZeebeSimpleAdminHandler zeebeSimpleAdminHandler(){
        return new ZeebeSimpleAdminHandler();
    }
}
