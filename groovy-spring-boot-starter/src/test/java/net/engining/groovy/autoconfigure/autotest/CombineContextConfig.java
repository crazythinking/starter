package net.engining.groovy.autoconfigure.autotest;

import net.engining.groovy.autoconfigure.autotest.support.FirstConfig;
import net.engining.groovy.autoconfigure.autotest.support.SecondConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 通用Context配置
 *
 * @author Eric Lu
 */
@Configuration
@Import({
        FirstConfig.class,
        SecondConfig.class
})
public class CombineContextConfig {

}
