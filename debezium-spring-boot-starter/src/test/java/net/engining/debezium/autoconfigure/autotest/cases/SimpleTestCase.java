package net.engining.debezium.autoconfigure.autotest.cases;


import net.engining.debezium.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

/**
 * @author Eric Lu
 **/
@ActiveProfiles(profiles = {
        "debezium.common",
        "debezium.xxljob.mysql",
        //"debezium.xxljob.oracle"
})
public class SimpleTestCase extends AbstractTestCaseTemplate {

    @Override
    public void initTestData() {

    }

    @Override
    public void assertResult() {
    }

    @Override
    public void testProcess() throws Exception {
        TimeUnit.SECONDS.sleep(1000); //睡眠100秒
    }

    @Override
    public void end() {

    }

}
