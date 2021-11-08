package net.engining.pg.disruptor.autoconfigure.autotest.support.group4;

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

@Component(Event4MultiSerialDisruptor.GROUP_NAME)
public class Event4MultiSerialDisruptor extends AbstractBizDataEventDisruptorEngine<Integer> {

    public final static String GROUP_NAME="Event4-Group";

    List<EventHandler<DisruptorBizDataEvent<Integer>>> eventHandlers = Lists.newArrayList();

    DisruptorBizDataEventFactory<Integer> eventFactory;

    /**
     * 通用的服务于{@link DisruptorBizDataEvent}的Disruptor构造函数
     *
     * @param applicationContext Spring application context
     */
    @Autowired
    public Event4MultiSerialDisruptor(ApplicationContext applicationContext, DisruptorProperties properties) {
        super(applicationContext, GROUP_NAME, ExecutionMode.MultiSerialChain);
        initProperties(properties);
        this.eventFactory = new DisruptorBizDataEventFactory<>();

        eventHandlers.add(new DisruptorHandler51(GROUP_NAME, 0, getBatchSize()));
        eventHandlers.add(new DisruptorHandler52(GROUP_NAME, 0, getBatchSize()));
        eventHandlers.add(new DisruptorHandler61(GROUP_NAME, 1, getBatchSize()));
        eventHandlers.add(new DisruptorHandler62(GROUP_NAME, 1, getBatchSize()));
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
