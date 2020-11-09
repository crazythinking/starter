package net.engining.bustream.config;

import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 *
 * @author : Eric Lu
 * @version :
 * @date : 2020-10-29 14:20
 * @since :
 **/
@Profile("bus.rabbit.bindings.output")
@PropertySource("classpath:application-bus.rabbit.bindings.output.yml")
public class BusRabbitBindingsOutPutConfig {
}
