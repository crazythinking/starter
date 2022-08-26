package net.engining.pg.disruptor.autoconfigure.autotest.cases;

import cn.hutool.core.lang.Console;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * 根据Benchmark的测试报告，BaseSubscriber#request(n)，n的大小与性能正相关；
 *
 * @author : Eric Lu
 * @version :
 * @date : 2021-03-07 20:23
 * @since :
 **/
public class ReactorTest {

    @Test
    @Benchmark
    public void range() {
        // [1] 构建一个发布者Flux; 注意，这时发布者还没开始生产数据。
        Flux<Integer> flux = Flux.range(1, 20);
        // [2] 构建一个订阅者Subscriber
        Subscriber<Integer> subscriber = new BaseSubscriber<Integer>() {
            protected void hookOnNext(Integer value) {
                Console.log(Thread.currentThread().getName() + " -> " + value);
                //从上游生产者请求指定数量的数据
                request(1);
            }
        };
        // [3] 创建订阅关系，这时，生产者开始生产数据，并传递给订阅者。
        flux.subscribe(subscriber);
    }

    @Test
    @Benchmark
    public void range2() {
        Flux<Integer> flux = Flux.range(1, 20);
        // [2] 构建一个订阅者Subscriber
        Subscriber<Integer> subscriber = new BaseSubscriber<Integer>() {
            protected void hookOnNext(Integer value) {
                Console.log(Thread.currentThread().getName() + " -> " + value);
                //从上游生产者请求指定数量的数据
                request(10);
            }
        };
        // [3] 创建订阅关系，这时，生产者开始生产数据，并传递给订阅者。
        flux.subscribe(subscriber);
    }

    @Test
    public void parallel() throws InterruptedException {
        Flux.range(0, 100)
                .parallel()
                .runOn(Schedulers.parallel())
                .subscribe(i -> {
                    Console.log(Thread.currentThread().getName() + " -> " + i);
                });
        //new CountDownLatch(1).await();
    }

    @Test
    public void parallel2(){
        Consumer myHandler = i -> {
            Console.log(Thread.currentThread().getName() + " receive:" + i);
        };

        Flux.range(1, 3)
                .doOnNext(i -> {
                    Console.log(Thread.currentThread().getName() + " doOnNext:" + i);
                })
                .publishOn(Schedulers.newParallel("myParallel"))
                .skip(1)
                .subscribe(myHandler);
    }

    @Test
    public void backpressure() throws InterruptedException {
        //1
        Flux.<Integer>create(sink -> {
                    for (int i = 0; i < 50; i++) {
                        System.out.println("push: " + i);
                        sink.next(i);
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                        }
                    }
                }, FluxSink.OverflowStrategy.ERROR)
                //2
                .publishOn(Schedulers.newSingle("receiver"), 10)
                .subscribe(new BaseSubscriber<Integer>() {
                    protected void hookOnSubscribe(Subscription subscription) {
                        subscription.request(1);
                    }
                    protected void hookOnNext(Integer value) {
                        System.out.println("receive:" + value);
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                        }
                        request(1);
                    }
                    protected void hookOnError(Throwable throwable) {
                        throwable.printStackTrace();
                        System.exit(1);
                    }
                });
        //new CountDownLatch(1).await();
    }

}
