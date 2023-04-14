package net.engining.bustream.base.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.util.MimeType;

import java.io.Serializable;


/**
 * 默认消息生产者的处理抽象类，由业务类继承并注入 Spring Context；
 * 由子类注入MessageChannel;
 *
 * @author Eric Lu
 * @date 2020-10-29 18:16
 **/
public abstract class AbstractProduceBustreamHandler<E extends Serializable> extends AbstractNewProduceBustreamHandler<E> {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProduceBustreamHandler.class);

    MessageChannel messageChannel;

    @Override
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    protected void setMessageChannel(MessageChannel messageChannel) {
        this.messageChannel = messageChannel;
    }

    @Override
    protected boolean send(Message<E> message, String bindingName, MimeType mimeType) {
        return messageChannel.send(message);
    }
}
