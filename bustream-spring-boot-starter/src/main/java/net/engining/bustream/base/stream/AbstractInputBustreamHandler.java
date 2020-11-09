package net.engining.bustream.base.stream;

import net.engining.bustream.base.BustreamHandler;
import net.engining.pg.support.utils.ExceptionUtilsExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;

import java.io.Serializable;
import java.util.Map;

/**
 * 默认消息消费者的处理抽象类，由业务系统继承并注入 Spring Context
 * @author Eric Lu
 * @date 2020-10-29 18:16
 **/
public abstract class AbstractInputBustreamHandler<E extends Serializable> implements BustreamHandler<E> {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractInputBustreamHandler.class);

    @Autowired
    @Qualifier(Sink.INPUT)
    SubscribableChannel subscribableChannel;

    /**
     * 接收消息，无需显示调用，由监听器自动触发
     * @param event 消息事件
     */
    @StreamListener(Sink.INPUT)
    protected void received(@Payload E event, @Headers Map<String, Object> headers){
        before(event);
        try {
            boolean rt = handler(event, headers);
            after(event, rt);

        } catch (Exception e) {
            ExceptionUtilsExt.dump(e);
            after(event, false);
        }

    }

    /**
     * 接收到消息事件后的处理逻辑
     * @param event 消息事件
     */
    protected abstract boolean handler(E event, Map<String, Object> headers);

    @Override
    public void before(E event) {
        defalutBefore(event, Type.CONSUMER, LOGGER);
    }

    @Override
    public void after(E event, boolean rt) {
        defaultAfter(event, rt, Type.CONSUMER, LOGGER);
    }

}
