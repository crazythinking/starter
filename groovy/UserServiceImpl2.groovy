import net.engining.groovy.autoconfigure.autotest.support.Gamer
import net.engining.groovy.autotest.services.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.annotation.Resource

class UserServiceImpl2 implements UserService {
    static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl2.class);

    @Resource
    Gamer gamer;

    @Override
    String hello(String name) {

        LOGGER.info("UserServiceImpl2: this is gamer")
        gamer.play()

        LOGGER.info("UserServiceImpl2: this is user service")
        return "hello " + name
    }
}