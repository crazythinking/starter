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
@Profile("bus.common.bindings.output")
@PropertySource("classpath:application-bus.common.bindings.output.yml")
public class BusCommonBindingsOutputConfig {
}
