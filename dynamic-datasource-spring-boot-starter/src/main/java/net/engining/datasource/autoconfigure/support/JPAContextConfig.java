package net.engining.datasource.autoconfigure.support;

import net.engining.datasource.autoconfigure.aop.SwitchOrg4HibernateFilterHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * JPA 相关的context配置；
 * 不再默认配置{@link org.springframework.data.jpa.repository.config.EnableJpaAuditing}；
 *
 * @author Administrator
 *
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Configuration
@Import(value = {
		JPA4H2ContextConfig.class
		})
public class JPAContextConfig {

	@Bean
	@ConditionalOnMissingBean
	public SwitchOrg4HibernateFilterHandler switchOrg4HibernateFilterHandler() {
		SwitchOrg4HibernateFilterHandler aspect = new SwitchOrg4HibernateFilterHandler();
		return aspect;
	}
}
