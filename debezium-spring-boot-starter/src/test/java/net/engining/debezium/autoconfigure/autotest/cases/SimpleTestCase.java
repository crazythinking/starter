package net.engining.debezium.autoconfigure.autotest.cases;


import net.engining.debezium.autoconfigure.autotest.support.AbstractTestCaseTemplate;

/**
 * @author Eric Lu
 **/
public class SimpleTestCase extends AbstractTestCaseTemplate {

    @Override
    public void initTestData() throws Exception {

    }

    @Override
    public void assertResult() throws Exception {
    }

    @Override
    public void testProcess() throws Exception {
        Thread.sleep(10000);
    }

    @Override
    public void end() throws Exception {

    }

}
