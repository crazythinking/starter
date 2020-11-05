package net.engining.bustream.base.stream;

import net.engining.bustream.base.BustreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.binder.PollableMessageSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.Serializable;

/**
 * 消息的轮询消费者抽象封装，由业务类继承并注入 Spring Context
 *
 * @author : Eric Lu
 * @version :
 * @date : 2020-10-29 19:28
 * @since :
 **/
public abstract class AbstractPollinputBustreamHandler<E extends Serializable> implements BustreamHandler<E> {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPollinputBustreamHandler.class);

    @Autowired
    @Qualifier(StreamPollableInput.POLLINPUT)
    PollableMessageSource pollableMessageSource;

    @Scheduled(fixedDelayString ="${gm.bustream.pollinput-fixed-delay:1000}")
    public void pollReceived(){

        pollableMessageSource.poll(
                getMessageHandler(),
                getParameterizedTypeReference()
        );

    }

    /**
     * 获取消息后的业务处理实现
     * @return  MessageHandler
     */
    protected abstract MessageHandler getMessageHandler();

    /**
     * 由业务子类用于指定具体的引用类型
     * @return ParameterizedTypeReference
     */
    protected abstract ParameterizedTypeReference getParameterizedTypeReference();

    @Override
    public void before(E event) {
        defalutBefore(event, Type.POLLABLE_CONSUMER, LOGGER);
    }

    @Override
    public void after(E event, boolean rt) {
        defaultAfter(event, rt, Type.POLLABLE_CONSUMER, LOGGER);
    }
}
