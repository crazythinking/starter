package net.engining.bustream.autotest.cases;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import net.engining.bustream.autotest.support.AbstractTestCaseTemplate;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
        "bus.disable",
        //"channel.stream.input.rabbit",
        //"channel.stream.output.rabbit",
        "channel.stream.input.kafka",
        "channel.stream.output.kafka",
        //"rabbit.dev",
        "kafka.dev",
        "stream.common",
        "channel.input.dev",
        "channel.output.dev",
        "not.auto.ack"
})
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
public class StreamTestCase extends AbstractTestCaseTemplate {

    @Autowired
    UserOutputStreamHandler userMsgOutputStreamHandler;

    @Autowired
    User2OutputStreamHandler user2MsgOutputStreamHandler;

    @Autowired
    UserInputStreamHandler userMsgInputStreamHandler;

    @Autowired
    User2InputBustreamHandler user2InputBustreamHandler;

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
        Assert.isTrue(userMsgInputStreamHandler.okCount.get()==5, () -> "msg count != "+ 5);
        Assert.isTrue(user2InputBustreamHandler.okCount.get()==0, () -> "msg count != 0");

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
            userMsgOutputStreamHandler.send(user, headerMap);
        }

        Map<String, Object> headerMap2 = Maps.newHashMap();
        User2 user2 = setupUser2();
        user2.setAge(0);
        headerMap2.put("messageType", "type2");
        user2MsgOutputStreamHandler.send(user2, headerMap2);

        //?????????????????????????????????????????????
        Thread.sleep(20000);
    }

    private User setupUser() {
        User user = new User();
        user.setUserId(System.currentTimeMillis());
        user.setName("user1.name");
        user.setAge(RandomUtil.randomInt(1, 100));

        return user;
    }

    private User2 setupUser2() {
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
