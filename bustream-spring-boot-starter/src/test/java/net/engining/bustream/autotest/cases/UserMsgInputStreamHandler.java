package net.engining.bustream.autotest.cases;

import net.engining.bustream.base.stream.AbstractInputBustreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;

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

    @Override
    protected boolean handler(User event, Map<String, Object> headers) {
        LOGGER.info("handler event for user object: {}", event);
        user = event;
        return true;
    }
}
