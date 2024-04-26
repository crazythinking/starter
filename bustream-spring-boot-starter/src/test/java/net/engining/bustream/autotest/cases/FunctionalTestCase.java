package net.engining.bustream.autotest.cases;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Maps;
import net.engining.bustream.autotest.cases.handler.User2StreamBridgeStreamHandler;
import net.engining.bustream.autotest.cases.handler.UserStreamBridgeStreamHandler;
import net.engining.bustream.autotest.support.AbstractTestCaseTemplate;
import net.engining.bustream.autotest.support.User;
import net.engining.bustream.autotest.support.User2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2023-03-03 9:41
 * @since :
 **/
@ActiveProfiles(profiles = {
        "bustream.func.dev",
        "bustream.func.userouter",
        "bustream.func.userouter2",
        "bustream.func.catchuser",
        "bustream.func.catchuser2",
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class FunctionalTestCase extends AbstractTestCaseTemplate {
    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionalTestCase.class);

    @Autowired
    UserStreamBridgeStreamHandler userStreamBridgeStreamHandler;

    @Autowired
    User2StreamBridgeStreamHandler user2StreamBridgeStreamHandler;

    @Override
    public void initTestData() throws Exception {

    }

    @Override
    public void assertResult() throws Exception {

    }

    @Override
    public void testProcess() throws Exception {
        //通过StreamBridge发送消息是同步模型
        for (int i = 0; i < 50; i++) {

            Map<String, Object> headerMap = Maps.newHashMap();

            boolean rt = userStreamBridgeStreamHandler.send(setupUser(), headerMap);
            Pair<String, Boolean> rt2 = user2StreamBridgeStreamHandler.sendAndGetId(setupUser2(), headerMap);
            LOGGER.warn("streamBridge send succeed: {}", rt);
            LOGGER.warn("streamBridge sendAndGetId succeed: {}-{}", rt2.getKey(), rt2.getValue());
        }

        User2 user = new User2();
        user.setUserId2(System.currentTimeMillis());
        user.setName2("user2.name");
        user.setAge(0);
        user2StreamBridgeStreamHandler.sendAndGetId(user, Maps.newHashMap());

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

    public static User setupUser() {
        User user = new User();
        user.setUserId(System.currentTimeMillis());
        user.setName("user1.name");
        user.setAge(RandomUtil.randomInt(1, 100));

        return user;
    }

    @Override
    public void end() throws Exception {

    }
}
