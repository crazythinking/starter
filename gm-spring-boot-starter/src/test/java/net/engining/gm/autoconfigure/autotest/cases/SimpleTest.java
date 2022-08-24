package net.engining.gm.autoconfigure.autotest.cases;


import cn.hutool.core.lang.Console;
import net.engining.gm.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import net.engining.gm.autoconfigure.autotest.support.Inter;
import net.engining.gm.autoconfigure.autotest.support.Inter2;
import net.engining.pg.support.core.context.ApplicationContextHolder;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * @author Eric Lu
 **/
public class SimpleTest extends AbstractTestCaseTemplate {

    @Autowired
    private ConfigurableListableBeanFactory beanFactory;

    @Autowired
    Inter2 inter2;

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
        Map<String, Inter> beansOfType = beanFactory.getBeansOfType(Inter.class);
        Console.log(beansOfType);

        inter2.test();
    }

    @Override
    public void end() {

    }

}
