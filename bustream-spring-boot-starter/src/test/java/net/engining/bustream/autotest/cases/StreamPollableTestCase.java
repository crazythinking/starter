package net.engining.bustream.autotest.cases;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import net.engining.bustream.autotest.support.AbstractTestCaseTemplate;
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
@ActiveProfiles(profiles={
        "rabbit.common",
        "rabbit.dev",
        "bus.disable",
        "bustream.rabbit.binders",
        "stream.common.bindings.pollinput",
        "stream.common.bindings.output",
        "stream.rabbit.bindings.pollinput",
        "stream.rabbit.bindings.output",
        "stream.common",
        "stream.dev"
})
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
public class StreamPollableTestCase extends AbstractTestCaseTemplate {

    @Autowired
    UserOutputStreamHandler userMsgOutputStreamHandler;

    @Autowired
    UserPollInputStreamHandler userMsgPollInputStreamHandler;

    @Override
    public void initTestData() throws Exception {

    }

    @Override
    public void assertResult() throws Exception {

        Assert.isTrue(
                this.testAssertDataContext.size() == userMsgPollInputStreamHandler.users.size(),
                "same size"
        );
    }

    @Override
    public void testProcess() throws Exception {
        User user1 = new User();
        user1.setUserId(System.currentTimeMillis());
        user1.setName("name1");
        user1.setAge(RandomUtil.randomInt(1, 100));
        this.testAssertDataContext.put("user1", JSON.toJSONString(user1));

        User user2 = new User();
        user2.setUserId(System.currentTimeMillis());
        user2.setName("name2");
        user2.setAge(RandomUtil.randomInt(1, 100));
        this.testAssertDataContext.put("user2", JSON.toJSONString(user2));

        Map<String, Object> headerMap = Maps.newHashMap();
        headerMap.put("gender", user1.getAge() % 2);

        userMsgOutputStreamHandler.send(user1, headerMap);
        userMsgOutputStreamHandler.send(user2, headerMap);

        //等待另一个线程获取到消息并赋值
        Thread.sleep(10000);
    }

    @Override
    public void end() throws Exception {

    }
}
