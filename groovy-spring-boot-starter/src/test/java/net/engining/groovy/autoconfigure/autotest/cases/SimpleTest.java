package net.engining.groovy.autoconfigure.autotest.cases;


import net.engining.groovy.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import net.engining.groovy.autotest.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author Eric Lu
 **/
public class SimpleTest extends AbstractTestCaseTemplate {

    @Autowired(required = false)
    private UserService userService;

    @Override
    public void initTestData() {

    }

    @Override
    public void assertResult() {
    }

    @Override
    public void testProcess() {
        if (userService != null) {
            System.out.println(userService.hello("dynamic language"));
        }
    }

    @Override
    public void end() {

    }

}
