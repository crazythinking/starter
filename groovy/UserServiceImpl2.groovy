import net.engining.groovy.autoconfigure.autotest.support.Gamer
import net.engining.groovy.autotest.services.UserService

import javax.annotation.Resource

class UserServiceImpl2 implements UserService {

    @Resource
    Gamer gamer;

    @Override
    String hello(String name) {

        println("UserServiceImpl2: this is gamer")
        gamer.play()

        println("UserServiceImpl2: this is user service")
        return "hello " + name
    }
}