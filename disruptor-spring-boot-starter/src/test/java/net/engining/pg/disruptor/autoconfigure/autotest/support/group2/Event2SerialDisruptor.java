package net.engining.pg.disruptor.autoconfigure.autotest.support.group2;

import com.google.common.collect.Lists;
import com.lmax.disruptor.EventHandler;
import net.engining.pg.disruptor.AbstractBizDataEventDisruptorEngine;
import net.engining.pg.disruptor.event.DisruptorBizDataEvent;
import net.engining.pg.disruptor.event.handler.ExecutionMode;
import net.engining.pg.disruptor.factory.DisruptorBizDataEventFactory;
import net.engining.pg.disruptor.props.DisruptorProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(Event2SerialDisruptor.GROUP_NAME)
public class Event2SerialDisruptor extends AbstractBizDataEventDisruptorEngine<Integer> {

    public final static String GROUP_NAME="Event2-Group";

    private final List<EventHandler<DisruptorBizDataEvent<Integer>>> eventHandlers = Lists.newArrayList();

    private DisruptorBizDataEventFactory<Integer> eventFactory;

    /**
     * 通用的服务于{@link DisruptorBizDataEvent}的Disruptor构造函数
     *
     * @param applicationContext Spring application context
     */
    @Autowired
    public Event2SerialDisruptor(ApplicationContext applicationContext, DisruptorProperties properties) {
        super(
                applicationContext,
                GROUP_NAME,
                ExecutionMode.SerialChain
        );
        initProperties(properties);
        this.eventFactory = new DisruptorBizDataEventFactory<>();

        //setup event handlers
        eventHandlers.add(new DisruptorHandler11(GROUP_NAME, this.getBatchSize()));
        eventHandlers.add(new DisruptorHandler12(GROUP_NAME, this.getBatchSize()));
        eventHandlers.add(new DisruptorHandler13(GROUP_NAME, this.getBatchSize()));

    }


    @Override
    public List<? extends EventHandler<DisruptorBizDataEvent<Integer>>> getEventHandlers() {
        return eventHandlers;
    }

    @Override
    public DisruptorBizDataEventFactory<Integer> getEventFactory() {
        return eventFactory;
    }

}
