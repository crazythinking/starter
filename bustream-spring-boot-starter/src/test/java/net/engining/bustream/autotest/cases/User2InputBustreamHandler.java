package net.engining.bustream.autotest.cases;

import net.engining.bustream.base.stream.AbstractConsume4ManualAckBustreamHandler;
import net.engining.pg.support.core.exception.ErrorCode;
import net.engining.pg.support.core.exception.ErrorMessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *  消费者子类，此类默认绑定了Channel -> INPUT；
 *
 * @author : Eric Lu
 * @version :
 * @date : 2021-05-04 11:16
 * @since :
 **/
@Profile({
        "channel.stream.input.rabbit",
        "channel.stream.input.kafka",
        "not.auto.ack"
})
@Service
public class User2InputBustreamHandler extends AbstractConsume4ManualAckBustreamHandler<User2> implements InitializingBean {

    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(User2InputBustreamHandler.class);

    public User2 user;

    public AtomicInteger okCount = new AtomicInteger(0);

    /**
     * 接收消息，无需显示调用，由监听器自动触发
     */
    @StreamListener(value = Sink.INPUT, condition = "headers['messageType']=='type2'")
    protected void consume4ChannelInput(@Payload User2 event, @Headers MessageHeaders headers) {
        receiving(event, headers);
    }

    @Override
    protected void handler(User2 event, Map<String, Object> headers) throws Exception{
        LOGGER.info("handler event for user2 object: {}", event);
        if (event.getAge()==0){
            user = event;
            throw new ErrorMessageException(ErrorCode.SystemError, "error test");
        }
        else {
            okCount.incrementAndGet();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.setLogger(LOGGER);
        super.setType(Type.CONSUMER);
    }
}
