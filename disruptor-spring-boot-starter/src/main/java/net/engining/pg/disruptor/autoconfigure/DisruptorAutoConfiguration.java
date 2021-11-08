package net.engining.pg.disruptor.autoconfigure;

import com.lmax.disruptor.dsl.Disruptor;
import net.engining.pg.disruptor.AbstractBizDataEventDisruptorEngine;
import net.engining.pg.disruptor.BizDataEventDisruptorTemplate;
import net.engining.pg.disruptor.event.translator.BizDataEventOneArgTranslator;
import net.engining.pg.disruptor.event.translator.BizKeyEventOneArgTranslator;
import net.engining.pg.disruptor.props.DisruptorProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Map;

/**
 * @author Eric Lu
 */
@SuppressWarnings("rawtypes")
@Configuration
@ConditionalOnClass({Disruptor.class})
@ConditionalOnProperty(prefix = DisruptorProperties.PREFIX, value = "enabled", matchIfMissing = true)
@EnableConfigurationProperties({DisruptorProperties.class})
@Import({
        EventListenerContextConfig.class
})
public class DisruptorAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public BizKeyEventOneArgTranslator bizKeyEventOneArgTranslator() {
        return new BizKeyEventOneArgTranslator();
    }

    @Bean
    @ConditionalOnMissingBean
    public BizDataEventOneArgTranslator bizDataEventOneArgTranslator() {
        return new BizDataEventOneArgTranslator();
    }

    @Bean
    @ConditionalOnMissingBean
    public BizDataEventDisruptorTemplate bizDataEventDisruptorTemplate(
            ApplicationContext applicationContext,
            BizDataEventOneArgTranslator bizDataEventOneArgTranslator
    ) {
        Map<String, AbstractBizDataEventDisruptorEngine> disruptors =
                applicationContext.getBeansOfType(AbstractBizDataEventDisruptorEngine.class);
        disruptors.forEach((s, abstractBizDataEventDisruptorWrapper) -> abstractBizDataEventDisruptorWrapper.start());
        return new BizDataEventDisruptorTemplate(disruptors, bizDataEventOneArgTranslator);
    }

}
