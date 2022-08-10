package net.engining.drools.autoconfigure;


import net.engining.drools.autoconfigure.props.GavConverter;
import net.engining.pg.drools.core.KieEngine;
import net.engining.pg.drools.props.GroupedDroolsProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Eric Lu
 */
@Configuration
@EnableConfigurationProperties(GroupedDroolsProperties.class)
@ConditionalOnProperty(prefix = GroupedDroolsProperties.PREFIX, value = "enabled", matchIfMissing = true)
public class DroolsAutoConfiguration {

    @Bean
    public GavConverter gavConverter() {
        return new GavConverter();
    }

    @Bean
    public KieEngine kieEngine(GroupedDroolsProperties groupedDroolsProperties) {
        return new KieEngine(groupedDroolsProperties);
    }

}
