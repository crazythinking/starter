package net.engining.bustream.autotest;

import com.google.common.collect.Maps;
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
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

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

	/**
	 * 不推荐
	 * 由spring.cloud.stream.poller.fixed-delay指定触发间隔，默认1s
	 */
	@Bean
	public Supplier<Message<User2>> addUser2(){
		return () -> {
			Message<User2> message = MessageBuilder.createMessage(
					FunctionalTestCase.setupUser2(),
					new MessageHeaders(Maps.newHashMap())
			);
			LOGGER.warn("Functional Producer[addUser2] send: {}", message.getPayload());
			return message;
		};
	}

	@Bean
	public Consumer<Message<User2>> catchUser2Lu() {
		return message -> {
			LOGGER.warn("Functional Consumer[catchUser2Lu] received: {}", message.getPayload());
		};
	}

	@Bean
	public Consumer<Message<User2>> catchUser2Repay1() {
		return message -> {
			LOGGER.warn("Functional Consumer[catchUser2Repay1] received: {}", message.getPayload());
		};
	}
}
