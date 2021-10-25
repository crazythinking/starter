package net.engining.gm.autoconfigure.autotest.cases;


import net.engining.gm.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import net.engining.pg.support.core.context.ApplicationContextHolder;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.springframework.core.task.TaskExecutor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.Assert;

/**
 * @author Eric Lu
 **/
@ActiveProfiles(profiles = {
        "async",
})
public class AsyncTest extends AbstractTestCaseTemplate {

    @Override
    public void initTestData() {

    }

    @Override
    public void assertResult() {
        TaskExecutor taskExecutor = ApplicationContextHolder.getBean("getAsyncExecutor");
        Assert.isTrue(ValidateUtilExt.isNotNullOrEmpty(taskExecutor), "should not be null");
    }

    @Override
    public void testProcess() {

    }

    @Override
    public void end() {

    }

}
