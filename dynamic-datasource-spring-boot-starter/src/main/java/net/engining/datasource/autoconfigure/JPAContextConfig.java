package net.engining.datasource.autoconfigure;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * JPA 相关的context配置；
 * 不再默认配置{@link org.springframework.data.jpa.repository.config.EnableJpaAuditing}；
 *
 * @author Administrator
 *
 */
@Configuration
@Import(value = {
		JPA4H2ContextConfig.class
		})
public class JPAContextConfig {

}
