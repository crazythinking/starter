package net.engining.pg.disruptor.autoconfigure.autotest.support.group3;

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

@Component(Event3DiamondDisruptor.GROUP_NAME)
public class Event3DiamondDisruptor extends AbstractBizDataEventDisruptorEngine<Integer> {

    public final static String GROUP_NAME="Event3-Group";

    List<EventHandler<DisruptorBizDataEvent<Integer>>> eventHandlers = Lists.newArrayList();

    DisruptorBizDataEventFactory<Integer> eventFactory;

    /**
     * 通用的服务于{@link DisruptorBizDataEvent}的Disruptor构造函数
     *
     * @param applicationContext Spring application context
     */
    @Autowired
    public Event3DiamondDisruptor(ApplicationContext applicationContext, DisruptorProperties properties) {
        super(applicationContext, GROUP_NAME, ExecutionMode.DependenciesDiamond);
        initProperties(properties);
        this.eventFactory = new DisruptorBizDataEventFactory<>();

        eventHandlers.add(new DisruptorHandler21(GROUP_NAME, 0, getBatchSize()));
        eventHandlers.add(new DisruptorHandler22(GROUP_NAME, 0, getBatchSize()));
        eventHandlers.add(new DisruptorHandler31(GROUP_NAME, 1, getBatchSize()));
        eventHandlers.add(new DisruptorHandler32(GROUP_NAME, 1, getBatchSize()));
        eventHandlers.add(new DisruptorHandler41(GROUP_NAME, 2, getBatchSize()));
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
