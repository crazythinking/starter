package net.engining.bustream.base.stream;

import com.rabbitmq.client.Channel;
import net.engining.pg.support.utils.ExceptionUtilsExt;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.integration.IntegrationMessageHeaderAccessor;
import org.springframework.integration.acks.AcknowledgmentCallback;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Map;

/**
 * 消费者子类，其只限用于手动Ack的消费者；同时注意：消费者需要配置为手动Ack；
 *
 * @author : Eric Lu
 * @version :
 * @date : 2021-05-04 13:44
 * @since :
 **/
public abstract class AbstractConsume4ManualAckBustreamHandler<E extends Serializable>
                                                        extends AbstractConsumeBustreamHandler<E> {

    /**
     * 接收处理消息，且在处理成功后进行ACK处理
     */
    protected void receiving(E event, Map<String, Object> headers) {
        //AMQP特有的
        Channel channel = (Channel) headers.get(AmqpHeaders.CHANNEL);
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);

        //Integration通用的
        AcknowledgmentCallback ackCallback
                = (AcknowledgmentCallback) headers.get(IntegrationMessageHeaderAccessor.ACKNOWLEDGMENT_CALLBACK);

        //Kafka特有的
        Acknowledgment acknowledgment = (Acknowledgment) headers.get("kafka_acknowledgment");

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
            else if(ValidateUtilExt.isNotNullOrEmpty(ackCallback)){
                if (ret){
                    //提交ACK
                    ackCallback.acknowledge(AcknowledgmentCallback.Status.ACCEPT);
                }
                else {
                    //这里不重新入列，避免消费端重复消费失败的消息
                    ackCallback.acknowledge(AcknowledgmentCallback.Status.REJECT);
                }
            }
            else if (ValidateUtilExt.isNotNullOrEmpty(acknowledgment)){
                if (ret){
                    //提交ACK
                    acknowledgment.acknowledge();
                }
                else {
                    //这里不重新入列，避免消费端重复消费失败的消息
                    acknowledgment.nack(1000);
                }
            }

        } catch (Exception e) {
            ExceptionUtilsExt.dump(e);
        }
    }
}
