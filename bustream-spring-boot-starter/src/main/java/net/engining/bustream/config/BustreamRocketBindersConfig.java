package net.engining.bustream.config;

import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2020-10-29 14:20
 * @since :
 **/
@Profile("bustream.rocket.binders")
@PropertySource("classpath:application-bustream.rocket.binders.yml")
public class BustreamRocketBindersConfig {

}
