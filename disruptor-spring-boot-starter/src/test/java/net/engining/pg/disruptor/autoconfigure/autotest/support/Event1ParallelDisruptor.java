package net.engining.pg.disruptor.autoconfigure.autotest.support;

import com.google.common.collect.Lists;
import com.lmax.disruptor.EventHandler;
import net.engining.pg.disruptor.AbstractBizDataEventDisruptorWrapper;
import net.engining.pg.disruptor.event.DisruptorBizDataEvent;
import net.engining.pg.disruptor.event.handler.ExecutionMode;
import net.engining.pg.disruptor.factory.DisruptorBizDataEventFactory;
import net.engining.pg.disruptor.props.DisruptorProperties;
import net.engining.pg.disruptor.util.WaitStrategys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(Event1ParallelDisruptor.GROUP_NAME)
public class Event1ParallelDisruptor extends AbstractBizDataEventDisruptorWrapper<String> {

    public final static String GROUP_NAME="Event1-Group";

    final static int BATCH_SIZE=1;

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
                BATCH_SIZE,
                ExecutionMode.Parallel,
                WaitStrategys.YIELDING_WAIT,
                properties.isMultiProducer()
        );
    }


    @Override
    public List<? extends EventHandler<DisruptorBizDataEvent<String>>> getEventHandlers() {
        List<EventHandler<DisruptorBizDataEvent<String>>> eventHandlers = Lists.newArrayList();
        eventHandlers.add(new DisruptorHandler1(GROUP_NAME, BATCH_SIZE));
        eventHandlers.add(new DisruptorHandler2(GROUP_NAME, BATCH_SIZE));
        eventHandlers.add(new DisruptorHandler3(GROUP_NAME, BATCH_SIZE));
        return eventHandlers;
    }

    @Override
    public DisruptorBizDataEventFactory<String> getEventFactory() {
        return new DisruptorBizDataEventFactory<>();
    }

}
