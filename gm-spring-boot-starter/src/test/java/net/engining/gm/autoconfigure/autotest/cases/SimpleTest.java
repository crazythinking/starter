package net.engining.gm.autoconfigure.autotest.cases;


import net.engining.gm.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import net.engining.pg.support.core.context.ApplicationContextHolder;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.assertj.core.api.Assertions;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.Assert;

/**
 * @author Eric Lu
 **/
public class SimpleTest extends AbstractTestCaseTemplate {

    @Override
    public void initTestData() {

    }

    @Override
    public void assertResult() {
        Assert.isTrue(
                ValidateUtilExt.isNotNullOrEmpty(ApplicationContextHolder.getBean("provider4Organization")),
                "should not be null"
        );
        Assert.isTrue(
                ValidateUtilExt.isNotNullOrEmpty(ApplicationContextHolder.getBean("log4jMarkerService")),
                "should not be null"
        );
        Assertions.assertThatThrownBy(() -> ApplicationContextHolder.getBean("getAsyncExecutor"));
    }

    @Override
    public void testProcess() {

    }

    @Override
    public void end() {

    }

}
