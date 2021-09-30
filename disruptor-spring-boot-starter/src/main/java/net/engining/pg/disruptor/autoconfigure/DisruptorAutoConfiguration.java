package net.engining.pg.disruptor.autoconfigure;

import com.lmax.disruptor.dsl.Disruptor;
import net.engining.pg.disruptor.AbstractBizDataEventDisruptorWrapper;
import net.engining.pg.disruptor.BizDataEventDisruptorTemplate;
import net.engining.pg.disruptor.event.DisruptorApplicationEvent;
import net.engining.pg.disruptor.event.translator.BizDataEventOneArgTranslator;
import net.engining.pg.disruptor.props.DisruptorProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author Eric Lu
 */
@SuppressWarnings("rawtypes")
@Configuration
@ConditionalOnClass({Disruptor.class})
@ConditionalOnProperty(prefix = DisruptorProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties({DisruptorProperties.class})
public class DisruptorAutoConfiguration {

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
        Map<String, AbstractBizDataEventDisruptorWrapper> disruptors =
                applicationContext.getBeansOfType(AbstractBizDataEventDisruptorWrapper.class);
        disruptors.forEach((s, abstractBizDataEventDisruptorWrapper) -> {
            abstractBizDataEventDisruptorWrapper.start();
        });
        return new BizDataEventDisruptorTemplate(disruptors, bizDataEventOneArgTranslator);
    }

    /**
     * DisruptorApplicationEvent的监听器：接收DisruptorApplicationEvent并将其携带的数据作为事件传递给Disruptor
     */
    @Bean
    public ApplicationListener<DisruptorApplicationEvent> disruptorApplicationEventListener(
            BizDataEventDisruptorTemplate bizDataEventDisruptorTemplate) {

        return appEvent -> {
            //利用Spring的ApplicationEvent机制，将ApplicationEvent作为Disruptor的事件数据载体，传递给Disruptor
            bizDataEventDisruptorTemplate.publishEvent(appEvent);
        };
    }

}
