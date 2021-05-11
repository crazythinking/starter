package net.engining.bustream.autotest.cases;

import net.engining.bustream.base.stream.AbstractOutputBustreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2020-10-31 18:20
 * @since :
 **/
@Profile("stream.common.bindings.output")
@Service
public class UserOutputStreamHandler extends AbstractOutputBustreamHandler<User> implements InitializingBean {

    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserOutputStreamHandler.class);

    @Override
    protected void transform(User event, Map<String, Object> headers) {
        LOGGER.info("transform event to user object: {}", event);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.setLogger(LOGGER);
    }
}
