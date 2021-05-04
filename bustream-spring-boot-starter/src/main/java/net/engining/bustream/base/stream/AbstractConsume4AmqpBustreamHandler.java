package net.engining.bustream.base.stream;

import com.rabbitmq.client.Channel;
import net.engining.pg.support.utils.ExceptionUtilsExt;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.integration.IntegrationMessageHeaderAccessor;
import org.springframework.integration.acks.AcknowledgmentCallback;
import org.springframework.integration.amqp.inbound.AmqpMessageSource;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Map;

/**
 * 消费者子类，其只限用于AMQP协议的MQ；
 *
 * @author : Eric Lu
 * @version :
 * @date : 2021-05-04 13:44
 * @since :
 **/
public abstract class AbstractConsume4AmqpBustreamHandler<E extends Serializable>
                                                        extends AbstractConsumeBustreamHandler<E> {

    /**
     * 接收处理消息，且在处理成功后进行ACK处理
     */
    protected void receiving(E event, Map<String, Object> headers) {
        Channel channel = (Channel) headers.get(AmqpHeaders.CHANNEL);
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        AmqpMessageSource.AmqpAckCallback amqpAckCallback
                = (AmqpMessageSource.AmqpAckCallback) headers.get(IntegrationMessageHeaderAccessor.ACKNOWLEDGMENT_CALLBACK);

        boolean ret = received(event, headers);
        try {
            if (ValidateUtilExt.isNotNullOrEmpty(channel)){
                Assert.notNull(deliveryTag, AmqpHeaders.DELIVERY_TAG+" should not be null!!!");
                if (ret){
                    //提交ACK
                    channel.basicAck(deliveryTag, false);
                }
                else {
                    //这里不重新入列，避免消费端重复消费失败的消息
                    channel.basicNack(deliveryTag, false, false);
                }
            }
            else if(ValidateUtilExt.isNotNullOrEmpty(amqpAckCallback)){
                if (ret){
                    //提交ACK
                    amqpAckCallback.acknowledge(AcknowledgmentCallback.Status.ACCEPT);
                }
                else {
                    //这里不重新入列，避免消费端重复消费失败的消息
                    amqpAckCallback.acknowledge(AcknowledgmentCallback.Status.REJECT);
                }
            }

        } catch (Exception e) {
            ExceptionUtilsExt.dump(e);
        }
    }
}
