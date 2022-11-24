import net.engining.groovy.autoconfigure.autotest.support.Gamer
import net.engining.groovy.autotest.services.UserService
import org.springframework.beans.factory.annotation.Autowired

class UserServiceImpl implements UserService {

    @Autowired
    private Gamer gamer;

    @Override
    String hello(String name) {

        println("this is gamer")
        gamer.play()

        println("this is user service")
        return "hello " + name
    }
}