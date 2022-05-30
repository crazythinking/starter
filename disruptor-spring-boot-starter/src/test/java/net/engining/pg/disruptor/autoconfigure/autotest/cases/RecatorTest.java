package net.engining.pg.disruptor.autoconfigure.autotest.cases;

import cn.hutool.core.lang.Console;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.function.Consumer;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-03-07 20:23
 * @since :
 **/
public class RecatorTest {

    @Test
    public void range() {
        // [1] 构建一个发布者Flux; 注意，这时发布者还没开始生产数据。
        Flux<Integer> flux = Flux.range(1, 10);
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
    public void range2() {
        Flux<Integer> flux = Flux.range(1, 10);
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

}
