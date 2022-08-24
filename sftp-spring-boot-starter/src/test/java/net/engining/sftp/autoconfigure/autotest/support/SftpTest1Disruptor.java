package net.engining.sftp.autoconfigure.autotest.support;

import com.google.common.collect.Lists;
import com.lmax.disruptor.EventHandler;
import net.engining.pg.disruptor.AbstractBizDataEventDisruptorEngine;
import net.engining.pg.disruptor.event.DisruptorBizDataEvent;
import net.engining.pg.disruptor.event.handler.ExecutionMode;
import net.engining.pg.disruptor.factory.DisruptorBizDataEventFactory;
import net.engining.pg.disruptor.props.DisruptorProperties;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.util.List;

import static net.engining.sftp.autoconfigure.autotest.CombineContextConfig.SFTP_TEST_1;
public class SftpTest1Disruptor extends AbstractBizDataEventDisruptorEngine<File> {

    private DisruptorBizDataEventFactory<File> eventFactory;

    private final List<EventHandler<DisruptorBizDataEvent<File>>> eventHandlers = Lists.newArrayList();

    /**
     * 针对处理SFTP同步器产生的File消息，消息的BizData类型为{@link File}的Disruptor
     *
     * @param applicationContext Spring application context
     */
    public SftpTest1Disruptor(ApplicationContext applicationContext, DisruptorProperties properties) {
        super(applicationContext, SFTP_TEST_1, ExecutionMode.SerialChain);
        initProperties(properties);
        this.eventFactory = new DisruptorBizDataEventFactory<>();

        //setup event handlers
        eventHandlers.add(new SftpTest1FileDisruptorHandler01(SFTP_TEST_1, this.getBatchSize()));
    }

    @Override
    public List<? extends EventHandler<DisruptorBizDataEvent<File>>> getEventHandlers() {
        return eventHandlers;
    }

    @Override
    public DisruptorBizDataEventFactory<File> getEventFactory() {
        return eventFactory;
    }
}
