package net.engining.bustream.autotest.cases;

import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Maps;
import net.engining.bustream.autotest.cases.autoack.UserInput4AutoAckStreamHandler;
import net.engining.bustream.autotest.support.AbstractTestCaseTemplate;
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
        "channel.stream.input.rabbit",
        "channel.stream.output.rabbit",
        //"channel.stream.input.kafka",
        //"channel.stream.output.kafka",
        "rabbit.dev",
        //"kafka.dev",
        "stream.common",
        "channel.input.dev",
        "channel.output.dev",
})
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
public class Stream4AutoAckTestCase extends AbstractTestCaseTemplate {

    @Autowired
    UserOutputStreamHandler userMsgOutputStreamHandler;

    @Autowired
    User2OutputStreamHandler user2MsgOutputStreamHandler;

    @Autowired
    UserInput4AutoAckStreamHandler userMsgInputStreamHandler;

    @Autowired
    RabbitTemplate template;

    @Override
    public void initTestData() throws Exception {

    }

    @Override
    public void assertResult() throws Exception {

        //for autoAck, have one error
        Assert.isTrue(userMsgInputStreamHandler.okCount.get()==4, () -> "msg count != "+ 4);

    }

    @Override
    public void testProcess() throws Exception {
        autoAck();
        unacceptableMsg();
    }

    private void unacceptableMsg() throws Exception {
        MessageProperties properties1 = new MessageProperties();
        properties1.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        template.send(
                "bustream-test.default",
                "repayBack",
                new Message("1112333232323".getBytes(), properties1)
        );

        //?????????????????????????????????????????????
        Thread.sleep(10000);
    }

    private void autoAck() throws InterruptedException {
        User user = setupUser();
        Map<String, Object> headerMap = Maps.newHashMap();
        headerMap.put("gender", user.getAge() % 2);
        int n = 5;
        for (int i = 0; i < n; i++) {
            //last one set for error
            if (i==n-1){
                user.setAge(0);
            }
            userMsgOutputStreamHandler.send(user, headerMap);
        }

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
