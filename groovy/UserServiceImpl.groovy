import net.engining.groovy.autoconfigure.autotest.support.Gamer
import net.engining.groovy.autotest.services.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.annotation.Resource

class UserServiceImpl implements UserService {
    static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    Gamer gamer;

    @Override
    String hello(String name) {

        LOGGER.info("this is gamer")
        gamer.play()

        LOGGER.info("this is user service")
        return "hello " + name
    }
}