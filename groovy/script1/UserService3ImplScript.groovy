import net.engining.groovy.autoconfigure.autotest.support.Gamer
import net.engining.groovy.autotest.services3.UserService3
import org.example.MainDeps

class UserService3Impl implements UserService3{

    Gamer gamer
    UserService3Impl(Gamer gamer) {
        this.gamer = gamer
    }


    @Override
    String hello(String name) {

        MainDeps obj = new MainDeps()
        obj.main("1")

        println("service3, this is gamer")
        gamer.play()

        println("this is user service3")
        return "service3, hello " + name
    }
}

def user = new UserService3Impl(gamer)
