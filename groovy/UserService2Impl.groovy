import net.engining.groovy.autoconfigure.autotest.support.Gamer
import net.engining.groovy.autotest.services2.UserService2
import org.springframework.beans.factory.annotation.Autowired

class UserService2Impl implements UserService2 {

    @Autowired
    Gamer gamer;

    @Override
    String hello(String name) {

        println("UserService2, this is gamer")
        gamer.play()

        println("this is user service2")
        return "hello " + name
    }
}