package net.engining.bustream.autotest.cases;

import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Maps;
import net.engining.bustream.autotest.support.AbstractTestCaseTemplate;
import net.engining.bustream.autotest.support.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

/**
 * 同类型多 Binder 测试
 *
 * @author : Eric Lu
 * @version :
 * @date : 2023-03-03 9:41
 * @since :
 **/
@ActiveProfiles(profiles = {
        "bustream.func.dev",
        "bustream.rabbit1.binders",
        "bustream.rabbit2.binders",
        //"bustream.func.userouter",
        //"bustream.func.catchuser",
        "bustream.func.adduser2",
        "bustream.func.catchuser2lu",
        //"bustream.func.catchuser2chu"

})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class FunctionalBindersTestCase extends AbstractTestCaseTemplate {
    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionalBindersTestCase.class);

    //@Autowired
    //UserStreamBridgeStreamHandler userStreamBridgeStreamHandler;

    @Override
    public void initTestData() throws Exception {

    }

    @Override
    public void assertResult() throws Exception {

    }

    @Override
    public void testProcess() throws Exception {

        //通过StreamBridge发送消息是同步模型
        for (int i = 0; i < 15; i++) {

            Map<String, Object> headerMap = Maps.newHashMap();

            //boolean rt = userStreamBridgeStreamHandler.send(setupUser(), headerMap);
            //LOGGER.warn("streamBridge send succeed: {}", rt);
        }

        //等待另一个线程获取到消息并赋值
        Thread.sleep(20000);
    }

    private static User setupUser() {
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
