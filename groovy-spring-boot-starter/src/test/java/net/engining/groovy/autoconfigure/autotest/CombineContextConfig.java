package net.engining.groovy.autoconfigure.autotest;

import net.engining.groovy.autoconfigure.annotations.EnableGroovyScript;
import org.springframework.context.annotation.Configuration;

/**
 * 通用Context配置
 *
 * @author Eric Lu
 */
@Configuration
@EnableGroovyScript(basePackages = {
        "net.engining.groovy.autotest.services"
})
public class CombineContextConfig {


}
