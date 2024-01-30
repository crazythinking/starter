package net.engining.bustream.base.stream;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.lang.UUID;
import net.engining.bustream.base.BustreamHandler;
import net.engining.bustream.base.ErrorMessageEvent;
import net.engining.pg.support.utils.ExceptionUtilsExt;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.ClassUtils;
import org.springframework.util.MimeType;

import java.io.Serializable;
import java.util.Map;


/**
 * 消息生产者的处理抽象类，由业务类继承并注入 Spring Context；
 *
 * @author Eric Lu
 **/
public abstract class AbstractNewProduceBustreamHandler<E extends Serializable> implements BustreamHandler<E> {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNewProduceBustreamHandler.class);

    Logger logger = LOGGER;

    Type type = Type.PRODUCER;

    protected String bindingName;

    protected MimeType mimeType;

    protected ApplicationEventPublisher publisher;

    @Override
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void setType(Type type) {
        this.type = type;
    }

    protected void setPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * 发送消息事件并返回结果，需要业务逻辑端显示调用
     *
     * @param event   消息事件
     * @param headers 消息头信息
     */
    public boolean send(E event, Map<String, Object> headers) {
        return sendAndGetId(event, headers).getValue();
    }

    public Pair<String, Boolean> sendAndGetId(E event, Map<String, Object> headers) {
        boolean ret = false;
        String messageId = null;
        Message<E> message = null;
        defaultBefore(event, type, logger);
        try {
            if (ValidateUtilExt.isNullOrEmpty(headers.get(ORIGIN_MESSAGE_ID))){
                messageId = UUID.fastUUID().toString(true);
                headers.put(ORIGIN_MESSAGE_ID, messageId);
            }
            else {
                messageId = (String) headers.get(ORIGIN_MESSAGE_ID);
            }
            //TODO 由于是通用逻辑，所以如果classpath中都包含了这些类，那么header中可能包含多个类型的messageId
            if (ClassUtils.isPresent(
                    "org.springframework.amqp.support.AmqpHeaders", this.getClass().getClassLoader())
            ){
                headers.put(AmqpHeaders.MESSAGE_ID, messageId);
            }
            if (ClassUtils.isPresent(
                    "org.springframework.kafka.support.KafkaHeaders", this.getClass().getClassLoader())){
                headers.put(KafkaHeaders.MESSAGE_KEY, messageId);
            }
            if (ClassUtils.isPresent(
                    "com.alibaba.cloud.stream.binder.rocketmq.support.RocketMQHeaderMapper", this.getClass().getClassLoader())){
                headers.put("rocketmq_MESSAGE_ID", messageId);
            }

            transform(event, headers);
            message = MessageBuilder.createMessage(event, new MessageHeaders(headers));
            ret = send(message,bindingName,mimeType);
            defaultAfter(event, ret, type, logger);

        } catch (Exception e) {
            ExceptionUtilsExt.dump(e);
            this.publisher.publishEvent(new ErrorMessageEvent(new ErrorMessage(e, message)));
            defaultAfter(event, false, type, logger);
        }
        return Pair.of(messageId, ret);
    }

    protected abstract boolean send(Message<E> message, String bindingName, MimeType mimeType);

    /**
     * 对要发送的消息事件进行自定义转换处理
     *
     * @param event 消息事件
     * @param headers Map
     *
     */
    protected abstract void transform(E event, Map<String, Object> headers);

    @Override
    public void before(E event) {
        //do nothing
    }

    @Override
    public void after(E event, boolean rt) {
        //do nothing
    }

}
