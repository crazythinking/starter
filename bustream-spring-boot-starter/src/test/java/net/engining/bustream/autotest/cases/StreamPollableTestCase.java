package net.engining.bustream.autotest.cases;

import net.engining.bustream.autotest.cases.handler.UserPollInputStreamHandler;
import net.engining.bustream.autotest.support.AbstractTestCaseTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2020-10-31 18:25
 * @since :
 **/
@ActiveProfiles(profiles={
        "channel.pollinput.dev",
})
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
public class StreamPollableTestCase extends AbstractTestCaseTemplate {

    @Autowired
    UserPollInputStreamHandler userMsgPollInputStreamHandler;

    @Override
    public void initTestData() throws Exception {

    }

    @Override
    public void assertResult() throws Exception {

    }

    @Override
    public void testProcess() throws Exception {

        //等待另一个线程获取到消息并赋值
        Thread.sleep(10000);
    }

    @Override
    public void end() throws Exception {

    }
}
