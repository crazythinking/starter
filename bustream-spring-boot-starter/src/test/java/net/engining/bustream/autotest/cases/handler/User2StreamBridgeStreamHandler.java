package net.engining.bustream.autotest.cases.handler;

import net.engining.bustream.autotest.support.User2;
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
        "bustream.func.userouter2",
})
@Service
public class User2StreamBridgeStreamHandler extends AbstractStreamBridgeBustreamHandler<User2> implements InitializingBean {

    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(User2StreamBridgeStreamHandler.class);

    @Autowired
    public User2StreamBridgeStreamHandler(StreamBridge streamBridge, ApplicationEventPublisher publisher) {
        super(streamBridge, "user-outer2", MimeTypeUtils.APPLICATION_JSON, publisher);
    }

    @Override
    protected void transform(User2 event, Map<String, Object> headers) {
        headers.put("messageType", "type2");
        LOGGER.info("transform event to user object: {}", event);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.setLogger(LOGGER);
    }
}
