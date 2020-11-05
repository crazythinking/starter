package net.engining.bustream.autotest.cases;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import net.engining.bustream.base.stream.AbstractInputBustreamHandler;
import net.engining.bustream.base.stream.AbstractPollinputBustreamHandler;
import net.engining.pg.support.utils.ExceptionUtilsExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2020-10-31 18:52
 * @since :
 **/
@Profile("stream.common.bindings.pollinput")
@Service
public class UserMsgPollInputStreamHandler extends AbstractPollinputBustreamHandler<User> {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserMsgPollInputStreamHandler.class);

    public List<User> users = Lists.newArrayList();

    private boolean handler(User event, MessageHeaders messageHeaders) {
        LOGGER.info("handler event for user object: {}", event);
        users.add(event);
        return true;
    }

    @Override
    protected MessageHandler getMessageHandler() {
        return message -> {
            User event = (User) message.getPayload();
            MessageHeaders messageHeaders = message.getHeaders();
            before(event);
            try {
                boolean ret = handler(event, messageHeaders);
                after(event, ret);
            } catch (Exception e) {
                ExceptionUtilsExt.dump(e);
                after(event, false);
            }
        };
    }

    @Override
    protected ParameterizedTypeReference<User> getParameterizedTypeReference() {
        return ParameterizedTypeReference.forType(User.class);
    }
}
