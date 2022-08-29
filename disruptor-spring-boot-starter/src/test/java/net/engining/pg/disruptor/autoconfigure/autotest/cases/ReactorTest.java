package net.engining.pg.disruptor.autoconfigure.autotest.cases;

import cn.hutool.core.lang.Console;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;

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
                //结合-Dreactor.logging.fallback=true，可打印每一个在其上游的Flux或Mono的事件
                .log()
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

    @Test
    public void synGenerate() {
        Flux<String> flux = Flux.generate(
                () -> 0,
                (state, sink) -> {
                    sink.next("3 x " + state + " = " + 3*state);
                    if (state == 10) sink.complete();
                    return state + 1;
                },
                //只在最后触发一次，所以通常可在这个点触发清理state对应的资源
                (state) -> System.out.println("state: " + state)
        );
        Subscriber<String> subscriber = new BaseSubscriber<String>() {
            protected void hookOnNext(String value) {
                Console.log(Thread.currentThread().getName() + " -> " + value);
                //从上游生产者请求指定数量的数据
                request(1);
            }
        };
        // [3] 创建订阅关系，这时，生产者开始生产数据，并传递给订阅者。
        flux.subscribe(subscriber);
    }

    @Test
    public void testAppendBoomError() {
        //由于被测试方法需要一个 Flux，定义一个简单的 Flux 用于测试
        Flux<String> source = Flux.just("foo", "bar");

        //创建一个 StepVerifier 构造器来包装和校验一个 Flux
        StepVerifier.create(
                    //传进来需要测试的 Flux（即待测方法的返回结果）
                    appendBoomError(source)
                )
                //第一个我们期望的信号是 onNext，它的值为 foo
                .expectNext("foo")
                .expectNext("bar")
                //最后我们期望的是一个终止信号 onError，异常内容应该为 boom
                .expectErrorMessage("boom")
                //不要忘了使用 verify() 触发测试
                .verify();
    }

    private <T> Flux<T> appendBoomError(Flux<T> source) {
        return source.concatWith(Mono.error(new IllegalArgumentException("boom")));
    }

    @Test
    public void testSplitPathIsUsed() {
        StepVerifier.create(
                processOrFallback(
                        Mono.just("just a  phrase with    tabs!"),
                        Mono.just("EMPTY_PHRASE")
                ))
                .expectNext("just", "a", "phrase", "with", "tabs!")
                .verifyComplete();
    }

    @Test
    public void testEmptyPathIsUsed() {
        StepVerifier.create(
                processOrFallback(
                        Mono.empty(),
                        Mono.just("EMPTY_PHRASE")
                ))
                .expectNext("EMPTY_PHRASE")
                .verifyComplete();
    }

    private Flux<String> processOrFallback(Mono<String> source, Publisher<String> fallback) {
        return source
                .flatMapMany(phrase -> Flux.fromArray(phrase.split("\\s+")))
                .switchIfEmpty(fallback);
    }

    @Test
    public void testCommandEmptyPathIsUsed() {
        //	创建一个探针（probe），它会转化为一个空序列
        PublisherProbe<Void> probe = PublisherProbe.empty();

        //在需要使用 Mono<Void> 的位置调用 probe.mono() 来替换为探针
        StepVerifier.create(
                processOrFallback(
                        Mono.empty(),
                        probe.mono()
                ))
                .verifyComplete();

        //序列结束之后，你可以用这个探针来判断序列是如何使用的，你可以检查是它从哪（条路径）被订阅的
        probe.assertWasSubscribed();
        //请求也是一样的
        probe.assertWasRequested();
        //是否被取消了
        probe.assertWasNotCancelled();
    }

    private Mono<String> executeCommand(String command) {
        return Mono.just(command + " DONE");
    }

    private Mono<Void> processOrFallback(Mono<String> commandSource, Mono<Void> doWhenEmpty) {
        return commandSource
                .flatMap(command -> executeCommand(command).then())
                .switchIfEmpty(doWhenEmpty);
    }
}
