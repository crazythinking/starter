package net.engining.groovy.autoconfigure.autotest.support;

import net.engining.groovy.autoconfigure.annotations.EnableGroovyScript;
import org.springframework.context.annotation.Configuration;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2022-11-24 15:27
 * @since :
 **/
@Configuration
@EnableGroovyScript(basePackages = {
        "net.engining.groovy.autotest.services"
})
public class FirstConfig {
}
