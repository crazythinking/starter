package net.engining.bustream.autotest.cases;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import net.engining.bustream.autotest.support.AbstractTestCaseTemplate;
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
@ActiveProfiles(profiles={
        "rabbit.common",
        "rabbit.dev",
        "bus.disable",
        "bustream.rabbit.binders",
        "stream.common.bindings.input",
        "stream.common.bindings.output",
        "stream.rabbit.bindings.input",
        "stream.rabbit.bindings.output"
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
    }

    @Override
    public void testProcess() throws Exception {
        User user = new User();
        user.setUserId(System.currentTimeMillis());
        user.setName("name");
        user.setAge(RandomUtil.randomInt(1, 100));
        this.testAssertDataContext.put("user", JSON.toJSONString(user));

        Map<String, Object> headerMap = Maps.newHashMap();
        headerMap.put("gender", user.getAge() % 2);
        userMsgOutputStreamHandler.send(user, headerMap);

        //等待另一个线程获取到消息并赋值
        Thread.sleep(2000);
    }

    @Override
    public void end() throws Exception {

    }
}
