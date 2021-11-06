package net.engining.pg.disruptor.autoconfigure;


import net.engining.pg.disruptor.BizDataEventDisruptorTemplate;
import net.engining.pg.disruptor.event.DisruptorApplicationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class EventListenerContextConfig {

    @Autowired
    BizDataEventDisruptorTemplate bizDataEventDisruptorTemplate;

    /**
     * DisruptorApplicationEvent的监听器：接收DisruptorApplicationEvent并将其携带的数据作为事件传递给Disruptor
     */
    @EventListener
    public void disruptorApplicationEventListener(DisruptorApplicationEvent event) {
        bizDataEventDisruptorTemplate.publishEvent(event);
    }

}
