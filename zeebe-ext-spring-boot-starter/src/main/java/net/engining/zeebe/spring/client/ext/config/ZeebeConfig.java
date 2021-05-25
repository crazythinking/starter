package net.engining.zeebe.spring.client.ext.config;

import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-05-25 15:31
 * @since :
 **/
@Profile("zeebe")
@PropertySource("classpath:application-zeebe.yml")
public class ZeebeConfig {
}
