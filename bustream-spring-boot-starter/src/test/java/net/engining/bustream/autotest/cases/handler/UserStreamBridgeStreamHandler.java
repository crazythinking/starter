package net.engining.bustream.autotest.cases.handler;

import net.engining.bustream.autotest.support.User;
import net.engining.bustream.base.stream.AbstractStreamBridgeBustreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.util.Map;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2020-10-31 18:20
 * @since :
 **/
@Profile({
        "bustream.func.common",
})
@Service
public class UserStreamBridgeStreamHandler extends AbstractStreamBridgeBustreamHandler<User> implements InitializingBean {

    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserStreamBridgeStreamHandler.class);

    @Autowired
    public UserStreamBridgeStreamHandler(StreamBridge streamBridge, ApplicationEventPublisher publisher) {
        super(streamBridge, "user-outer", MimeTypeUtils.APPLICATION_JSON, publisher);
    }

    @Override
    protected void transform(User event, Map<String, Object> headers) {
        LOGGER.info("transform event to user object: {}", event);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.setLogger(LOGGER);
    }
}
