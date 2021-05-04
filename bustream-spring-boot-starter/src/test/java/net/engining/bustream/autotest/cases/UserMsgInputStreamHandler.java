package net.engining.bustream.autotest.cases;

import net.engining.bustream.base.stream.AbstractInputBustreamHandler;
import net.engining.pg.support.core.exception.ErrorCode;
import net.engining.pg.support.core.exception.ErrorMessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2020-10-31 18:52
 * @since :
 **/
@Profile("stream.common.bindings.input")
@Service
public class UserMsgInputStreamHandler extends AbstractInputBustreamHandler<User> {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserMsgInputStreamHandler.class);

    public User user;

    public AtomicInteger okCount = new AtomicInteger(0);

    @Override
    protected void handler(User event, Map<String, Object> headers) throws Exception{
        LOGGER.info("handler event for user object: {}", event);
        if (event.getAge()==0){
            user = event;
            throw new ErrorMessageException(ErrorCode.SystemError, "error test");
        }
        else {
            okCount.incrementAndGet();
        }
    }

    @Override
    public void before(User event) {
        defalutBefore(event, Type.CONSUMER, LOGGER);
    }

    @Override
    public void after(User event, boolean rt) {
        defaultAfter(event, rt, Type.CONSUMER, LOGGER);
    }
}
