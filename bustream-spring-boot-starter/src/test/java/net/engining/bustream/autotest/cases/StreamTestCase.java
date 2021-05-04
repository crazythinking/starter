package net.engining.bustream.autotest.cases;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import net.engining.bustream.autotest.support.AbstractTestCaseTemplate;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2020-10-31 18:25
 * @since :
 **/
@ActiveProfiles(profiles = {
        "rabbit.common",
        "rabbit.dev",
        "bus.disable",
        "bustream.rabbit.binders",
        "stream.common.bindings.input",
        "stream.common.bindings.output",
        "stream.rabbit.bindings.input",
        "stream.rabbit.bindings.output",
        "stream.common",
        "stream.dev"
})
public class StreamTestCase extends AbstractTestCaseTemplate {

    @Autowired
    UserMsgOutputStreamHandler userMsgOutputStreamHandler;

    @Autowired
    UserMsgInputStreamHandler userMsgInputStreamHandler;

    @Override
    public void initTestData() throws Exception {

    }

    @Override
    public void assertResult() throws Exception {

        Assert.isTrue(
                this.testAssertDataContext.get("user").equals(JSON.toJSONString(userMsgInputStreamHandler.user)),
                "same user"
        );

        Assert.isTrue(userMsgInputStreamHandler.okCount.get()==10, "10 ok message received");

    }

    @Override
    public void testProcess() throws Exception {
        deadLetter();
        normal();
    }

    private void deadLetter() {
        User user = setupUser();
        user.setAge(0);
        this.testAssertDataContext.put("user", JSON.toJSONString(user));
        userMsgOutputStreamHandler.send(user, Maps.newHashMap());
    }

    private void normal() throws InterruptedException {
        User user = setupUser();

        Map<String, Object> headerMap = Maps.newHashMap();
        headerMap.put("gender", user.getAge() % 2);
        for (int i = 0; i < 10; i++) {
            userMsgOutputStreamHandler.send(user, headerMap);
        }

        //等待另一个线程获取到消息并赋值
        Thread.sleep(20000);
    }

    private User setupUser() {
        User user = new User();
        user.setUserId(System.currentTimeMillis());
        user.setName("name");
        user.setAge(RandomUtil.randomInt(1, 100));

        return user;
    }

    @Override
    public void end() throws Exception {

    }
}
