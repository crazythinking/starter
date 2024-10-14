import net.engining.groovy.autoconfigure.autotest.support.Gamer
import net.engining.groovy.autotest.services2.UserService2
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.annotation.Resource

class UserService2Impl implements UserService2 {

    static final Logger LOGGER = LoggerFactory.getLogger(UserService2Impl.class);

    @Resource
    Gamer gamer;

    @Override
    String hello(String name) {

        LOGGER.info("UserService2, this is gamer")
        gamer.play()

        LOGGER.info("this is user service2")
        return "hello " + name
    }
}