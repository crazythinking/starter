package net.engining.bustream.autotest.cases;

import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Maps;
import net.engining.bustream.autotest.cases.handler.UserInputStreamHandler;
import net.engining.bustream.autotest.cases.handler.UserOutputStreamHandler;
import net.engining.bustream.autotest.support.AbstractTestCaseTemplate;
import net.engining.bustream.autotest.support.User;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
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
        "channel.output.dev",
        "channel.input.dev",
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class StreamTestCase extends AbstractTestCaseTemplate {

    @Autowired
    UserOutputStreamHandler userOutputStreamHandler;

    @Autowired
    UserInputStreamHandler userInputStreamHandler;

    @Override
    public void initTestData() throws Exception {

    }

    @Override
    public void assertResult() throws Exception {
        //for deadLetter;
        //Assert.isTrue(
        //        this.testAssertDataContext.get("user").equals(JSON.toJSONString(userMsgInputStreamHandler.user)),
        //        "same user"
        //);

        //for normal
        Assert.isTrue(userInputStreamHandler.okCount.get() == 5, () -> "msg count != " + 5);

    }

    @Override
    public void testProcess() throws Exception {
        normal();
    }

    private void normal() throws InterruptedException {
        User user = setupUser();
        Map<String, Object> headerMap = Maps.newHashMap();
        headerMap.put("gender", user.getAge() % 2);
        headerMap.put("messageType", "type1");
        MessageProperties properties1 = new MessageProperties();
        properties1.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        int n = 5;
        for (int i = 0; i < n; i++) {
            properties1.getHeaders().putAll(headerMap);
            userOutputStreamHandler.send(user, headerMap);
        }
        user = new User();
        user.setUserId(System.currentTimeMillis());
        user.setName("user1.name");
        user.setAge(0);
        userOutputStreamHandler.send(user, headerMap);

        //等待另一个线程获取到消息并赋值
        Thread.sleep(20000);
    }

    private User setupUser() {
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
