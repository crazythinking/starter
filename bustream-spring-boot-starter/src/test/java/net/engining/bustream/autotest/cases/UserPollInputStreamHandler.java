package net.engining.bustream.autotest.cases;

import com.google.common.collect.Lists;
import net.engining.bustream.base.stream.AbstractPollinputBustreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2020-10-31 18:52
 * @since :
 **/
@Profile("stream.common.bindings.pollinput")
@Service
public class UserPollInputStreamHandler extends AbstractPollinputBustreamHandler<User> implements InitializingBean {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserPollInputStreamHandler.class);

    public List<User> users = Lists.newArrayList();

    @Override
    protected ParameterizedTypeReference<User> getParameterizedTypeReference() {
        return ParameterizedTypeReference.forType(User.class);
    }

    @Override
    protected void handler(User event, Map<String, Object> headers) throws Exception {
        LOGGER.info("handler event for user object: {}", event);
        users.add(event);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.setLogger(LOGGER);
    }
}
