package net.engining.drools.autoconfigure.autotest.cases;

import cn.hutool.core.lang.Console;
import net.engining.drools.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import net.engining.pg.drools.beans.ResultBean;
import net.engining.pg.drools.core.KieEngine;
import net.engining.sacl.drools.Person;
import org.kie.api.runtime.StatelessKieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-03-16 17:27
 * @since :
 **/
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
public class SimpleTestCase extends AbstractTestCaseTemplate {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    KieEngine kieEngine;

    @Override
    public void initTestData() {

    }

    @Override
    public void assertResult() {

    }

    @Override
    public void testProcess() {
        Person person = new Person("Jack",16);
        person.setDesc("combine xxx");
        StatelessKieSession session = kieEngine.statelessExecute(
                "sacl-drools",
                "session1",
                person
        );
        ResultBean resultBean = kieEngine.getResult(session);
        Console.log(resultBean.toString());

        person = new Person("Tom",50);
        person.setDesc("com xxx");
        session = kieEngine.statelessExecute(
                "sacl-drools-script",
                "session1",
                person
        );
        ResultBean resultBean2 = kieEngine.getResult(session);
        Console.log(resultBean2.toString());
    }

    @Override
    public void end() {

    }
}
