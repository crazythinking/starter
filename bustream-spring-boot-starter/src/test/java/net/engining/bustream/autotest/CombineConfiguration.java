package net.engining.bustream.autotest;

import net.engining.gm.config.props.GmCommonProperties;
import net.engining.pg.props.CommonProperties;
import net.engining.pg.support.core.context.ApplicationContextHolder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

@Configuration
@EnableConfigurationProperties(value = {
		CommonProperties.class,
		GmCommonProperties.class,
		})
@Import(value = {
		})
public class CombineConfiguration {

	@Bean
	@Lazy(value=false)
	public ApplicationContextHolder applicationContextHolder(){
		return new ApplicationContextHolder();
	}

}
