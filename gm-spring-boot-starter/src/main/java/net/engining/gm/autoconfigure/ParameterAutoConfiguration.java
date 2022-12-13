package net.engining.gm.autoconfigure;

import net.engining.gm.config.ParameterContextConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-10-21 12:24
 * @since :
 **/
@Configuration
@ConditionalOnProperty(prefix = "gm.config.enabled", name = "parameter", havingValue = "true")
@Import(value = {ParameterContextConfig.class})
@EntityScan(basePackages = {"net.engining.pg.parameter.entity"})
public class ParameterAutoConfiguration {
}
