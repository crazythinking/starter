package net.engining.pg.disruptor.autoconfigure.autotest.cases;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-03-17 15:29
 * @since :
 **/
public class MainDisruptor {

    public static class LongEventTranslator implements EventTranslatorOneArg<LongEvent, Long> {

        @Override
        public void translateTo(LongEvent event, long sequence, Long arg0) {
            event.set(arg0);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int bufferSize = 1024 * 1024;//环形队列长度，必须是2的N次方
        EventFactory<LongEvent> eventFactory = LongEvent.FACTORY;
        /**
         * 定义Disruptor，基于单生产者，阻塞策略
         */
        Disruptor<LongEvent> parallelDisruptor = new Disruptor<>(
                eventFactory,
                bufferSize,
                Executors.defaultThreadFactory(),
                ProducerType.SINGLE,
                new BlockingWaitStrategy()
        );
        Disruptor<LongEvent> serialDisruptor = new Disruptor<>(
                eventFactory,
                bufferSize,
                Executors.defaultThreadFactory(),
                ProducerType.SINGLE,
                new BlockingWaitStrategy()
        );
        Disruptor<LongEvent> diamondDisruptor = new Disruptor<>(
                eventFactory,
                bufferSize,
                Executors.defaultThreadFactory(),
                ProducerType.SINGLE,
                new BlockingWaitStrategy()
        );
        Disruptor<LongEvent> chainDisruptor = new Disruptor<>(
                eventFactory,
                bufferSize,
                Executors.defaultThreadFactory(),
                ProducerType.SINGLE,
                new BlockingWaitStrategy()
        );
        Disruptor<LongEvent> parallelPooldisruptor = new Disruptor<>(
                eventFactory,
                bufferSize,
                Executors.defaultThreadFactory(),
                ProducerType.SINGLE,
                new BlockingWaitStrategy()
        );
        Disruptor<LongEvent> serialPoolDisruptor = new Disruptor<>(
                eventFactory,
                bufferSize,
                Executors.defaultThreadFactory(),
                ProducerType.SINGLE,
                new BlockingWaitStrategy()
        );

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");//设置日期格式
        //System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
        //System.out.println("并行计算");
        //parallel(parallelDisruptor);
        //RingBuffer<LongEvent> parallelRingBuffer = parallelDisruptor.getRingBuffer();
        //parallelRingBuffer.publishEvent(new LongEventTranslator(), 10L);
        //parallelRingBuffer.publishEvent(new LongEventTranslator(), 100L);
        //
        //Thread.sleep(5000);

        System.out.println("");
        System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
        System.out.println("串行依次计算");
        serial(serialDisruptor);
        RingBuffer<LongEvent> serialRingBuffer = serialDisruptor.getRingBuffer();
        serialRingBuffer.publishEvent(new LongEventTranslator(), 20L);
        serialRingBuffer.publishEvent(new LongEventTranslator(), 200L);

        //Thread.sleep(5000);
        //
        //System.out.println("");
        //System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
        //System.out.println("菱形计算.");
        //diamond(diamondDisruptor);
        //RingBuffer<LongEvent> diamondRingBuffer = diamondDisruptor.getRingBuffer();
        //diamondRingBuffer.publishEvent(new LongEventTranslator(), 30L);
        //diamondRingBuffer.publishEvent(new LongEventTranslator(), 300L);
        //
        //Thread.sleep(5000);
        //
        //System.out.println("");
        //System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
        //System.out.println("链式并行计算");
        //chain(chainDisruptor);
        //RingBuffer<LongEvent> chainRingBuffer = chainDisruptor.getRingBuffer();
        //chainRingBuffer.publishEvent(new LongEventTranslator(), 40L);
        //chainRingBuffer.publishEvent(new LongEventTranslator(), 400L);
        //
        //Thread.sleep(5000);
        //
        //System.out.println("");
        //System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
        //System.out.println("并行计算实现,c1,c2互相不依赖");
        //parallelWithPool(parallelPooldisruptor);
        //RingBuffer<LongEvent> parallelPoolRingBuffer = parallelPooldisruptor.getRingBuffer();
        //parallelPoolRingBuffer.publishEvent(new LongEventTranslator(), 50L);
        //parallelPoolRingBuffer.publishEvent(new LongEventTranslator(), 500L);
        //
        //Thread.sleep(5000);
        //
        //System.out.println("");
        //System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
        //System.out.println("串行依次执行,同时C11，C21分别有2个实例");
        //serialWithPool(serialPoolDisruptor);
        //RingBuffer<LongEvent> serialPoolRingBuffer = serialPoolDisruptor.getRingBuffer();
        //serialPoolRingBuffer.publishEvent(new LongEventTranslator(), 60L);
        //serialPoolRingBuffer.publishEvent(new LongEventTranslator(), 600L);

    }

    public static class Add20EventHandler implements EventHandler<LongEvent>, WorkHandler<LongEvent> {

        @Override
        public void onEvent(LongEvent event, long sequence, boolean endOfBatch) throws Exception {
            onEvent(event);
        }

        @Override
        public void onEvent(LongEvent event) throws Exception {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");//设置日期格式
            String datestr = df.format(new Date());
            long n = event.get()+20;
            System.out.println(datestr + "+20.result=" + n);

        }
    }

    public static class Add10EventHandler implements EventHandler<LongEvent>, WorkHandler<LongEvent> {

        @Override
        public void onEvent(LongEvent event, long sequence, boolean endOfBatch) throws Exception {
            onEvent(event);
        }

        @Override
        public void onEvent(LongEvent event) throws Exception {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");//设置日期格式
            String datestr = df.format(new Date());
            long n = event.get()+10;
            System.out.println(datestr + "+10.result=" + n);
        }
    }

    public static class Muti10EventHandler implements EventHandler<LongEvent>, WorkHandler<LongEvent> {

        @Override
        public void onEvent(LongEvent event, long sequence, boolean endOfBatch) throws Exception {
            onEvent(event);
        }

        @Override
        public void onEvent(LongEvent event) throws Exception {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");//设置日期格式
            String datestr = df.format(new Date());
            long n = event.get()*10;
            System.out.println(datestr + "*10.result=" + n);
        }
    }

    public static class Muti20EventHandler implements EventHandler<LongEvent>, WorkHandler<LongEvent> {

        @Override
        public void onEvent(LongEvent event, long sequence, boolean endOfBatch) throws Exception {
            onEvent(event);
        }

        @Override
        public void onEvent(LongEvent event) throws Exception {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");//设置日期格式
            String datestr = df.format(new Date());
            long n = event.get()*20;
            System.out.println(datestr + "*20.result=" + n);
        }
    }

    /**
     * 并行计算实现,c1,c2互相不依赖
     * <br/>
     * p --> c11
     * --> c21
     */
    public static void parallel(Disruptor<LongEvent> disruptor) {
        disruptor.handleEventsWith(new Add20EventHandler(), new Add10EventHandler());
        disruptor.start();
    }

    /**
     * 串行依次执行
     * <br/>
     * p --> c11 --> c21
     *
     * @param disruptor
     */
    public static void serial(Disruptor<LongEvent> disruptor) {
        disruptor.handleEventsWith(new Add20EventHandler())
                .then(new Add10EventHandler())
                .then(new Muti10EventHandler());
        disruptor.start();
    }

    /**
     * 菱形方式执行
     * <br/>
     * --> c11
     * p          --> c21
     * --> c12
     *
     * @param disruptor
     */
    public static void diamond(Disruptor<LongEvent> disruptor) {
        disruptor.handleEventsWith(new Add20EventHandler(), new Muti10EventHandler()).then(new Add10EventHandler());
        disruptor.start();
    }

    /**
     * 链式并行计算
     * <br/>
     * --> c11 --> c12
     * p
     * --> c21 --> c22
     *
     * @param disruptor
     */
    public static void chain(Disruptor<LongEvent> disruptor) {
        System.out.println();
        disruptor.handleEventsWith(new Add20EventHandler()).then(new Muti10EventHandler());
        disruptor.handleEventsWith(new Add10EventHandler()).then(new Muti20EventHandler());
        disruptor.start();
    }

    /**
     * 并行计算实现,c1,c2互相不依赖,同时C1，C2分别有2个实例
     * <br/>
     * p --> c11
     * --> c21
     */
    public static void parallelWithPool(Disruptor<LongEvent> disruptor) {

        disruptor.handleEventsWithWorkerPool(new Add20EventHandler(), new Add20EventHandler());
        disruptor.handleEventsWithWorkerPool(new Add10EventHandler(), new Add10EventHandler());
        disruptor.start();
    }

    /**
     * 串行依次执行,同时C11，C21分别有2个实例
     * <br/>
     * p --> c11 --> c21
     *
     * @param disruptor
     */
    public static void serialWithPool(Disruptor<LongEvent> disruptor) {
        disruptor.handleEventsWithWorkerPool(new Add20EventHandler(), new Add20EventHandler()).then(new Add10EventHandler(), new Add10EventHandler());
        disruptor.start();
    }
}
