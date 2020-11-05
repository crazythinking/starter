package net.engining.bustream.base.stream;

import net.engining.bustream.base.BustreamHandler;
import net.engining.pg.support.utils.ExceptionUtilsExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

import java.io.Serializable;
import java.util.Map;


/**
 * 默认消息生产者的处理抽象类，由业务类继承并注入 Spring Context
 * @author Eric Lu
 * @date 2020-10-29 18:16
 **/
public abstract class AbstractOutputBustreamHandler<E extends Serializable> implements BustreamHandler<E> {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractOutputBustreamHandler.class);

    @Autowired
    @Qualifier(Source.OUTPUT)
    MessageChannel messageChannel;

    /**
     * 发送消息事件并返回结果，需要业务逻辑端显示调用
     * @param event     消息事件
     * @param headers   消息头信息
     */
    public void send(E event, Map<String, Object> headers){
        before(event);
        try {
            transform(event, headers);
            boolean ret = messageChannel.send(MessageBuilder.createMessage(event, new MessageHeaders(headers)));
            after(event, ret);
        } catch (Exception e) {
            ExceptionUtilsExt.dump(e);
            after(event, false);
        }

    }

    /**
     * 对要发送的消息事件进行自定义转换处理
     * @param event 消息事件
     */
    protected abstract void transform(E event, Map<String, Object> headers);

    @Override
    public void before(E event) {
        defalutBefore(event, Type.PRODUCER, LOGGER);
    }

    @Override
    public void after(E event, boolean rt) {
        defaultAfter(event, rt, Type.PRODUCER, LOGGER);
    }
}
