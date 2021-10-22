package net.engining.gm.autoconfigure;

import net.engining.gm.config.security.ActuatorWebSecurityConfigurerAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-10-22 11:05
 * @since :
 **/
@Configuration
@ConditionalOnProperty(prefix = "gm.config.enabled", name = "security", havingValue = "true")
@Import(value = {
        ActuatorWebSecurityConfigurerAdapter.class
})
public class WebSecurityAutoConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
