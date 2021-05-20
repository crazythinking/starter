package net.engining.bustream.autotest.cases;

import net.engining.bustream.base.stream.AbstractConsume4AmqpBustreamHandler;
import net.engining.pg.support.core.exception.ErrorCode;
import net.engining.pg.support.core.exception.ErrorMessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2020-10-31 18:52
 * @since :
 **/
@Profile({"stream.common.bindings.input", "stream.common"})
@Service
public class UserInputStreamHandler extends AbstractConsume4AmqpBustreamHandler<User> implements InitializingBean {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserInputStreamHandler.class);

    public List<User> users = new ArrayList<>();

    public AtomicInteger okCount = new AtomicInteger(0);

    /**
     * 接收消息，无需显示调用，由监听器自动触发
     */
    @StreamListener(value = Sink.INPUT, condition = "headers['messageType']=='type1'")
    protected void consume4ChannelInput(@Payload User event, @Headers MessageHeaders headers){
        receiving(event, headers);
    }

    @Override
    protected void handler(User event, Map<String, Object> headers) throws Exception{
        LOGGER.info("handler event for user1 object: {}", event);
        if (event.getAge()==0){
            throw new ErrorMessageException(ErrorCode.SystemError, "error test");
        }
        else {
            users.add(event);
            okCount.incrementAndGet();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.setLogger(LOGGER);
        super.setType(Type.CONSUMER);
    }
}
