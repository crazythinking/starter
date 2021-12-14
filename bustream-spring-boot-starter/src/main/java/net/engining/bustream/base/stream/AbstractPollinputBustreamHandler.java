package net.engining.bustream.base.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.binder.PollableMessageSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.Serializable;

/**
 * 轮询消费者子类，此类默认绑定了Channel -> POLLINPUT；
 * 如果业务类需要监听其他自定义的Channel时，需要自行实现{@link AbstractConsume4ManualAckBustreamHandler}；
 *
 * @author : Eric Lu
 * @version :
 * @date : 2020-10-29 19:28
 * @since :
 **/
public abstract class AbstractPollinputBustreamHandler<E extends Serializable>
                                                    extends AbstractConsumeBustreamHandler<E> {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPollinputBustreamHandler.class);

    PollableMessageSource pollableMessageSource;

    public AbstractPollinputBustreamHandler() {
        super();
        super.setLogger(LOGGER);
        super.setType(Type.POLLABLE_CONSUMER);
    }

    @Autowired
    @Qualifier(StreamPollableInput.POLLINPUT)
    public void setPollableMessageSource(PollableMessageSource pollableMessageSource) {
        this.pollableMessageSource = pollableMessageSource;
    }

    @Scheduled(fixedDelayString ="${gm.bustream.pollinput-fixed-delay:1000}")
    public void pollConsume4ChannelPollinput(){
        pollableMessageSource.poll(
                message -> {
                    received((E) message.getPayload(), message.getHeaders());
                },
                getParameterizedTypeReference()
        );

    }

    /**
     * 由业务子类用于指定具体的引用类型
     * @return ParameterizedTypeReference
     */
    protected abstract ParameterizedTypeReference getParameterizedTypeReference();

}
