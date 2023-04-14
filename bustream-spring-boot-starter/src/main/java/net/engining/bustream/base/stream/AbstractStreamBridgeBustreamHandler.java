package net.engining.bustream.base.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.util.MimeType;

import java.io.Serializable;


/**
 * 基于StreamBridge的消息生产者, 可简化Channel和Binding的定义；
 *
 * @author Eric Lu
 * @date 2020-10-29 18:16
 **/
public abstract class AbstractStreamBridgeBustreamHandler<E extends Serializable> extends AbstractNewProduceBustreamHandler<E> {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStreamBridgeBustreamHandler.class);

    private final StreamBridge streamBridge;

    public AbstractStreamBridgeBustreamHandler(StreamBridge streamBridge, String bindingName, MimeType mimeType,
                                               ApplicationEventPublisher publisher) {
        this.streamBridge = streamBridge;
        super.bindingName = bindingName;
        super.mimeType = mimeType;
        super.publisher = publisher;
    }

    @Override
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    protected boolean send(Message<E> message, String bindingName, MimeType mimeType) {
        return streamBridge.send(bindingName, message, mimeType);
    }

}
