package net.engining.bustream.autotest;

import net.engining.bustream.autotest.cases.FunctionalTestCase;
import net.engining.bustream.autotest.support.User2;
import net.engining.gm.config.props.GmCommonProperties;
import net.engining.pg.props.CommonProperties;
import net.engining.pg.support.core.context.ApplicationContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Configuration
@EnableConfigurationProperties(value = {
		CommonProperties.class,
		GmCommonProperties.class,
		})
@Import(value = {
		})
public class CombineConfiguration {
	/** logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(CombineConfiguration.class);

	@Bean
	@Lazy(value=false)
	public ApplicationContextHolder applicationContextHolder(){
		return new ApplicationContextHolder();
	}

	@Bean
	public Consumer<User2> catchUser2() {
		return user2 -> {
			LOGGER.warn("Functional Consumer Received: {}", user2.toString());
		};
	}

	@Bean
	public Supplier<User2> addUser2(){
		return FunctionalTestCase::setupUser2;
	}
}
