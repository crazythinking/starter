package net.engining.pg.disruptor.autoconfigure.autotest.support.group1;

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

@Component(Event1ParallelDisruptor.GROUP_NAME)
public class Event1ParallelDisruptor extends AbstractBizDataEventDisruptorEngine<String> {

    public final static String GROUP_NAME="Event1-Group";

    private final List<EventHandler<DisruptorBizDataEvent<String>>> eventHandlers = Lists.newArrayList();

    private DisruptorBizDataEventFactory<String> eventFactory;

    /**
     * 通用的服务于{@link DisruptorBizDataEvent}的Disruptor构造函数
     *
     * @param applicationContext Spring application context
     */
    @Autowired
    public Event1ParallelDisruptor(ApplicationContext applicationContext, DisruptorProperties properties) {
        super(
                applicationContext,
                GROUP_NAME,
                ExecutionMode.Parallel
        );
        initProperties(properties);
        this.eventFactory = new DisruptorBizDataEventFactory<>();

        //setup event handlers
        this.eventHandlers.add(new DisruptorHandler1(GROUP_NAME, this.getBatchSize()));
        this.eventHandlers.add(new DisruptorHandler2(GROUP_NAME, this.getBatchSize()));
        this.eventHandlers.add(new DisruptorHandler3(GROUP_NAME, this.getBatchSize()));
    }

    @Override
    public List<? extends EventHandler<DisruptorBizDataEvent<String>>> getEventHandlers() {
        return eventHandlers;
    }

    @Override
    public DisruptorBizDataEventFactory<String> getEventFactory() {
        return eventFactory;
    }

}
