package net.engining.bustream.base.stream;

import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;

import java.io.Serializable;

/**
 *  消费者子类，此类默认绑定了Channel -> INPUT；
 *  如果业务类需要监听其他自定义的Channel时，需要自行实现{@link AbstractConsume4AmqpBustreamHandler}；
 *
 * @author : Eric Lu
 * @version :
 * @date : 2021-05-04 11:16
 * @since :
 **/
public abstract class AbstractInputBustreamHandler<E extends Serializable>
                                                extends AbstractConsume4AmqpBustreamHandler<E> {
    /**
     * 接收消息，无需显示调用，由监听器自动触发
     */
    @StreamListener(Sink.INPUT)
    protected void consume4ChannelInput(@Payload E event, @Headers MessageHeaders headers){
        receiving(event, headers);
    }

}
