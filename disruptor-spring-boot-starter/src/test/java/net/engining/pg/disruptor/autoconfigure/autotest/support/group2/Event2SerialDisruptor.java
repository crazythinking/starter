package net.engining.pg.disruptor.autoconfigure.autotest.support.group2;

import com.google.common.collect.Lists;
import com.lmax.disruptor.EventHandler;
import net.engining.pg.disruptor.AbstractBizDataEventDisruptorWrapper;
import net.engining.pg.disruptor.autoconfigure.autotest.support.group1.DisruptorHandler1;
import net.engining.pg.disruptor.autoconfigure.autotest.support.group1.DisruptorHandler2;
import net.engining.pg.disruptor.autoconfigure.autotest.support.group1.DisruptorHandler3;
import net.engining.pg.disruptor.event.DisruptorBizDataEvent;
import net.engining.pg.disruptor.event.handler.ExecutionMode;
import net.engining.pg.disruptor.factory.DisruptorBizDataEventFactory;
import net.engining.pg.disruptor.props.DisruptorProperties;
import net.engining.pg.disruptor.props.GroupedDisruptorProperties;
import net.engining.pg.disruptor.util.DisruptorUtils;
import net.engining.pg.disruptor.util.WaitStrategys;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(Event2SerialDisruptor.GROUP_NAME)
public class Event2SerialDisruptor extends AbstractBizDataEventDisruptorWrapper<Integer> {

    public final static String GROUP_NAME="Event2-Group";

    final static int BATCH_SIZE=1;

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
    }


    @Override
    public List<? extends EventHandler<DisruptorBizDataEvent<Integer>>> getEventHandlers() {
        List<EventHandler<DisruptorBizDataEvent<Integer>>> eventHandlers = Lists.newArrayList();
        eventHandlers.add(new DisruptorHandler11(GROUP_NAME, BATCH_SIZE));
        eventHandlers.add(new DisruptorHandler12(GROUP_NAME, BATCH_SIZE));
        eventHandlers.add(new DisruptorHandler13(GROUP_NAME, BATCH_SIZE));
        return eventHandlers;
    }

    @Override
    public DisruptorBizDataEventFactory<Integer> getEventFactory() {
        return new DisruptorBizDataEventFactory<>();
    }

}
