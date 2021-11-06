package net.engining.pg.disruptor.autoconfigure.autotest.cases;

import cn.hutool.core.util.RandomUtil;
import net.engining.pg.disruptor.BizDataEventDisruptorTemplate;
import net.engining.pg.disruptor.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import net.engining.pg.disruptor.autoconfigure.autotest.support.group1.Event1ParallelDisruptor;
import net.engining.pg.disruptor.autoconfigure.autotest.support.group2.Event2SerialDisruptor;
import net.engining.pg.disruptor.autoconfigure.autotest.support.group3.Event3DiamondDisruptor;
import net.engining.pg.disruptor.autoconfigure.autotest.support.group4.Event4MultiSerialDisruptor;
import net.engining.pg.disruptor.event.GenericDisruptorApplicationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-03-16 17:27
 * @since :
 **/
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
public class DisruptorTest extends AbstractTestCaseTemplate {

    @Autowired
    BizDataEventDisruptorTemplate bizDataEventDisruptorTemplate;

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public void initTestData() {

    }

    @Override
    public void assertResult() {

    }

    @Override
    public void testProcess() throws Exception {
        GenericDisruptorApplicationEvent<String> event2 = new GenericDisruptorApplicationEvent<>(this);
        event2.setTopicKey(Event1ParallelDisruptor.GROUP_NAME);
        event2.setKey("key1");
        event2.setTag("en");
        event2.setBind("12345");
        for (int i = 0; i < 10; i++) {
            event2.setBind(event2.getBind()+1);
            applicationContext.publishEvent(event2);
        }

        GenericDisruptorApplicationEvent<Integer> event = new GenericDisruptorApplicationEvent<>(this);
        event.setTopicKey(Event2SerialDisruptor.GROUP_NAME);
        event.setKey("key1");
        event.setTag("en");
        event.setBind(12345);
        applicationContext.publishEvent(event);
        for (int i = 0; i < 10; i++) {
            event.setBind(event.getBind()+1);
            applicationContext.publishEvent(event);
        }

        bizDataEventDisruptorTemplate.publishEvent(
                this,
                Event3DiamondDisruptor.GROUP_NAME,
                "en",
                12345
        );

        for (int i = 0; i < 10; i++) {
            bizDataEventDisruptorTemplate.publishEvent(
                    this,
                    Event4MultiSerialDisruptor.GROUP_NAME,
                    "en",
                    RandomUtil.randomInt()
            );
        }

        Thread.sleep(5000);
    }

    @Override
    public void end() {

    }
}
