package net.engining.drools.autoconfigure.autotest.cases;

import net.engining.drools.autoconfigure.autotest.support.AbstractTestCaseTemplate;
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
public class SimpleTest extends AbstractTestCaseTemplate {

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public void initTestData() {

    }

    @Override
    public void assertResult() {

    }

    @Override
    public void testProcess() throws Exception {
    }

    @Override
    public void end() {

    }
}
