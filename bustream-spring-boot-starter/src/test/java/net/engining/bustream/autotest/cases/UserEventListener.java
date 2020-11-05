package net.engining.bustream.autotest.cases;

import net.engining.bustream.base.bus.AbstractRemoteApplicationEventListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2020-11-05 12:29
 * @since :
 **/
@Profile("bus.common.bindings.input")
@Service
public class UserEventListener extends AbstractRemoteApplicationEventListener<User> {

    public User user;

    @Override
    public boolean handler(User event) {
        user = event;
        return true;
    }
}
