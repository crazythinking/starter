package net.engining.pg.disruptor.autoconfigure.autotest.cases;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.thread.ThreadUtil;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import net.engining.pg.disruptor.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import net.engining.pg.disruptor.factory.DisruptorEventThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.DirtiesContext;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-03-16 17:27
 * @since :
 **/
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
public class SimpleTest extends AbstractTestCaseTemplate {

    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTest.class);

    @Override
    public void initTestData() throws Exception {

    }

    @Override
    public void assertResult() throws Exception {

    }

    @Override
    public void testProcess() throws Exception {
        testSimple2PmC();
    }

    @Override
    public void end() throws Exception {

    }

    public static class LongEvent {
        private long value;
        public void setValue(long value) {
            this.value = value;
        }

        public long getValue() {
            return value;
        }
    }


    public void testSimple2P2C() {
        Disruptor<LongEvent> disruptor = new Disruptor<>(
                LongEvent::new,
                1024,
                ThreadFactoryBuilder
                        .create()
                        .setNamePrefix("2PmC-Disruptor-Thread-")
                        .setThreadFactory(new DisruptorEventThreadFactory())
                        .build(),
                ProducerType.MULTI,
                new YieldingWaitStrategy()
        );

        disruptor.handleEventsWith(
                (event, sequence, endOfBatch) -> LOGGER.info("Event: " + event.getValue())
        );

        disruptor.start();

        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        ProducerWithTranslator producer = new ProducerWithTranslator(ringBuffer);

        Thread thread1 = new Thread(() -> {
            for (int i = 100; i < 110; i++) {
                producer.onData(i);
                ThreadUtil.sleep(1000);
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                producer.onData(i);
                ThreadUtil.sleep(1000);
            }
        });

        thread1.start();
        thread2.start();

        ThreadUtil.sleep(10000);
    }

    public void testSimple2PmC() {
        Disruptor<LongEvent> disruptor = new Disruptor<>(
                LongEvent::new,
                1024,
                ThreadFactoryBuilder
                        .create()
                        .setNamePrefix("2PmC-Disruptor-Thread-")
                        .setThreadFactory(new DisruptorEventThreadFactory())
                        .build(),
                ProducerType.MULTI,
                new YieldingWaitStrategy()
        );

        disruptor.handleEventsWithWorkerPool(
                (event) -> LOGGER.info("Event: " + event.getValue()),
                (event) -> LOGGER.info("Event: " + event.getValue()),
                (event) -> LOGGER.info("Event: " + event.getValue()),
                (event) -> LOGGER.info("Event: " + event.getValue())
        );

        disruptor.start();

        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        ProducerWithTranslator producer = new ProducerWithTranslator(ringBuffer);

        Thread thread1 = new Thread(() -> {
            for (int i = 100; i < 110; i++) {
                producer.onData(i);
                ThreadUtil.sleep(1000);
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                producer.onData(i);
                ThreadUtil.sleep(1000);
            }
        });

        thread1.start();
        thread2.start();

        ThreadUtil.sleep(10000);
    }

    public class ProducerWithTranslator implements EventTranslatorOneArg<LongEvent, Long> {
        RingBuffer<LongEvent> ringBuffer;

        public ProducerWithTranslator(RingBuffer<LongEvent> ringBuffer) {
            this.ringBuffer = ringBuffer;
        }

        public void onData(long i) {
            ringBuffer.publishEvent(this, i);
        }


        @Override
        public void translateTo(LongEvent event, long sequence, Long arg0) {
            event.setValue(arg0);
        }
    }

}
