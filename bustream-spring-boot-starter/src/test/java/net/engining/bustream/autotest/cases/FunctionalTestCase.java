package net.engining.bustream.autotest.cases;

import cn.hutool.core.util.RandomUtil;
import net.engining.bustream.autotest.support.AbstractTestCaseTemplate;
import net.engining.bustream.autotest.support.User2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.MimeTypeUtils;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2023-03-03 9:41
 * @since :
 **/
@ActiveProfiles(profiles = {
        "bustream.func.dev",
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class FunctionalTestCase extends AbstractTestCaseTemplate {
    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionalTestCase.class);

    @Autowired
    private StreamBridge streamBridge;

    @Override
    public void initTestData() throws Exception {

    }

    @Override
    public void assertResult() throws Exception {

    }

    @Override
    public void testProcess() throws Exception {
        //通过StreamBridge发送消息是同步模型
        for (int i = 0; i < 5; i++) {
            boolean rt = streamBridge.send("catchUser2-in-0", setupUser2(), MimeTypeUtils.APPLICATION_JSON);
            LOGGER.warn("streamBridge send succeed: {}", rt);
        }

        //等待另一个线程获取到消息并赋值
        Thread.sleep(20000);
    }

    public static User2 setupUser2() {
        User2 user = new User2();
        user.setUserId2(System.currentTimeMillis());
        user.setName2("user2.name");
        user.setAge(RandomUtil.randomInt(1, 100));

        return user;
    }

    @Override
    public void end() throws Exception {

    }
}
