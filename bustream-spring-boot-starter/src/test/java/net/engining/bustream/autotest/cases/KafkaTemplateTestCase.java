package net.engining.bustream.autotest.cases;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.RandomUtil;
import net.engining.bustream.autotest.support.AbstractTestCaseTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-12-01 10:16
 * @since :
 **/
@ActiveProfiles(profiles={
        "bus.disable",
        "bustream.kafka.binders",
        "kafka.dev"
})
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
public class KafkaTemplateTestCase extends AbstractTestCaseTemplate {

    @Autowired
    KafkaTemplate<String, User> kafkaTemplate;

    @Override
    public void initTestData() throws Exception {

    }

    @Override
    public void assertResult() throws Exception {

    }

    @Override
    public void testProcess() throws Exception {
        for (int i = 0; i < 100; i++) {
            kafkaTemplate.sendDefault(setupUser());
        }

        Console.log("finish");
    }

    @Override
    public void end() throws Exception {

    }

    private User setupUser() {
        User user = new User();
        user.setUserId(System.currentTimeMillis());
        user.setName("user1.name");
        user.setAge(RandomUtil.randomInt(1, 100));

        return user;
    }
}
