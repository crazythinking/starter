package net.engining.dyntp.autoconfigure.autotest.cases;


import net.engining.dyntp.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import net.engining.pg.support.core.context.ApplicationContextHolder;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.assertj.core.api.Assertions;
import org.dromara.dynamictp.core.executor.DtpExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.Assert;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Eric Lu
 **/
@ActiveProfiles({
        "dyntp.test"
})
public class SimpleTest extends AbstractTestCaseTemplate {

    @Override
    public void initTestData() {

    }

    @Override
    public void assertResult() {
        Assert.isTrue(
                ValidateUtilExt.isNotNullOrEmpty(ApplicationContextHolder.getBean("applicationTaskExecutor")),
                "should not be null"
        );
        Assert.isTrue(
                ValidateUtilExt.isNotNullOrEmpty(ApplicationContextHolder.getBeansOfType(Executor.class)),
                "should not be null"
        );
        Assert.isTrue(
                ValidateUtilExt.isNotNullOrEmpty(ApplicationContextHolder.getBeansOfType(ThreadPoolExecutor.class)),
                "should not be null"
        );
        Assert.isTrue(
                ValidateUtilExt.isNotNullOrEmpty(ApplicationContextHolder.getBeansOfType(DtpExecutor.class)),
                "should not be null"
        );
        //被DynamicTp注解的 applicationTaskExecutor Bean 不再是ThreadPoolTaskExecutor类型
        Assert.isTrue(
                ValidateUtilExt.isNullOrEmpty(ApplicationContextHolder.getBeansOfType(ThreadPoolTaskExecutor.class)),
                "should be null"
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
