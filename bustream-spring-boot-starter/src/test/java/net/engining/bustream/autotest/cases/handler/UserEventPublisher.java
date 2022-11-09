package net.engining.bustream.autotest.cases.handler;

import net.engining.bustream.autotest.support.User;
import net.engining.bustream.base.bus.AbstractRemoteApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2020-11-05 12:11
 * @since :
 **/
@Profile("bus.common.bindings.output")
@Service
public class UserEventPublisher extends AbstractRemoteApplicationEventPublisher<User> {

    @Override
    public void transform(User event) {
    }
}
