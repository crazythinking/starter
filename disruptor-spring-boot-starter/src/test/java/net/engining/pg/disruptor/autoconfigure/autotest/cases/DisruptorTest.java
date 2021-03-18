package net.engining.pg.disruptor.autoconfigure.autotest.cases;

import com.lmax.disruptor.dsl.Disruptor;
import net.engining.pg.disruptor.DisruptorTemplate;
import net.engining.pg.disruptor.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import net.engining.pg.disruptor.event.AbstractDisruptorEvent;
import net.engining.pg.disruptor.event.DisruptorApplicationEvent;
import net.engining.pg.disruptor.event.handler.ExecutionMode;
import net.engining.pg.disruptor.util.DisruptorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-03-16 17:27
 * @since :
 **/
public class DisruptorTest extends AbstractTestCaseTemplate {

    @Autowired
    DisruptorTemplate bizDataEventDisruptorTemplate;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    Map<String, Disruptor<AbstractDisruptorEvent>> disruptors;

    @Override
    public void initTestData() throws Exception {
        DisruptorApplicationEvent<String> event = new DisruptorApplicationEvent<>(this);
        event.setEventGroupKey(DisruptorUtils.groupKey("TestCase-Event1", ExecutionMode.Parallel));
        event.setKey("key1");
        event.setTag("en");
        event.setBind("this is biz data");
        this.testIncomeDataContext.put("event", event);

    }

    @Override
    public void assertResult() throws Exception {

    }

    @Override
    public void testProcess() throws Exception {
        DisruptorApplicationEvent<String> event = (DisruptorApplicationEvent<String>) this.testIncomeDataContext.get("event");
        //applicationContext.publishEvent(event);

        DisruptorApplicationEvent<Integer> event2 = new DisruptorApplicationEvent<>(this);
        event2.setEventGroupKey(DisruptorUtils.groupKey("TestCase-Event2", ExecutionMode.SerialChain));
        event2.setKey("key1");
        event2.setTag("en");
        event2.setBind(12345);

        //bizDataEventDisruptorTemplate.publishEvent(event);
        //bizDataEventDisruptorTemplate.publishEvent(event);
        //bizDataEventDisruptorTemplate.publishEvent(event2);

        //bizDataEventDisruptorTemplate.publishEvent(
        //        this,
        //        DisruptorUtils.groupKey("TestCase-Event3", ExecutionMode.DependenciesDiamond),
        //        "en",
        //        12345
        //);

        bizDataEventDisruptorTemplate.publishEvent(
                this,
                DisruptorUtils.groupKey("TestCase-Event4", ExecutionMode.MultiSerialChain),
                "en",
                12345
        );

        Thread.sleep(20000);
    }

    @Override
    public void end() throws Exception {

    }
}
