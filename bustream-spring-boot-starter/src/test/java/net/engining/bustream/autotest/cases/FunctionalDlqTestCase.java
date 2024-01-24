package net.engining.bustream.autotest.cases;

import net.engining.bustream.autotest.cases.handler.UserDlqConsumerFuncStreamHandler;
import net.engining.bustream.autotest.support.AbstractTestCaseTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2023-03-03 9:41
 * @since :
 **/
@ActiveProfiles(profiles = {
        "bustream.func.catchdlq"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class FunctionalDlqTestCase extends AbstractTestCaseTemplate {
    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionalDlqTestCase.class);

    @Autowired
    UserDlqConsumerFuncStreamHandler userDlqConsumerFuncStreamHandler;

    @Override
    public void initTestData() throws Exception {

    }

    @Override
    public void assertResult() throws Exception {

    }

    @Override
    public void testProcess() throws Exception {

        //等待另一个线程获取到消息并赋值
        Thread.sleep(20000);
    }

    @Override
    public void end() throws Exception {

    }
}
