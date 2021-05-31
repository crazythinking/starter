package net.engining.zeebe.spring.client.ext.autoconfigure;

import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.bean.value.factory.ReadZeebeWorkerValue;
import io.camunda.zeebe.spring.client.config.processor.ZeebeWorkerPostProcessor;
import net.engining.zeebe.spring.client.ext.ZeebeSimpleAdminHandler;
import net.engining.zeebe.spring.client.ext.config.ZeebeConfig;
import net.engining.zeebe.spring.client.ext.config.ZeebeWorkerExtPostProcessor;
import net.engining.zeebe.spring.client.ext.prop.ZeebeExtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

/**
 * @author : Eric Lu
 * @date : 2021-04-16 16:46
 **/
@Configuration
@EnableConfigurationProperties({
        ZeebeExtProperties.class
})
@Import(value = {
        ZeebeConfig.class
})
@EnableZeebeClient
public class ZeebeAutoConfiguration {

    @Bean
    public ZeebeSimpleAdminHandler zeebeSimpleAdminHandler(){
        return new ZeebeSimpleAdminHandler();
    }

    @Bean
    @Primary
    public ZeebeWorkerPostProcessor zeebeWorkerPostProcessor(final ReadZeebeWorkerValue reader,
                                                             ZeebeExtProperties zeebeExtProperties
    ) {
        return new ZeebeWorkerExtPostProcessor(reader,zeebeExtProperties);
    }
}
