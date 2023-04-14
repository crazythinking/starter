package net.engining.bustream.autotest.cases.handler;

import net.engining.bustream.autotest.support.User2;
import net.engining.bustream.base.stream.AbstractConsumeBustreamHandler;
import net.engining.pg.support.core.exception.ErrorCode;
import net.engining.pg.support.core.exception.ErrorMessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;


/**
 * @author : Eric Lu
 * @version :
 * @date : 2020-10-31 18:52
 * @since :
 **/
@Profile({
        "bustream.func.common",
})
@Service
public class UserConsumerFuncStreamHandler extends AbstractConsumeBustreamHandler<User2> implements InitializingBean {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserConsumerFuncStreamHandler.class);

    public List<User2> users = new ArrayList<>();

    public AtomicInteger okCount = new AtomicInteger(0);

    /**
     * 接收消息，无需显示调用，由监听器自动触发
     */
    @Bean
    public Consumer<Message<User2>> catchUser2() {
        return message -> {
            try {
                super.consume(message.getPayload(), message.getHeaders());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    protected void handler(User2 event, Map<String, Object> headers) throws Exception{
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
    }
}
