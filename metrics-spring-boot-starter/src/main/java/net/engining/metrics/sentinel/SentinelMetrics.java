package net.engining.metrics.sentinel;

import com.alibaba.csp.sentinel.metric.extension.MetricExtension;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import net.engining.metrics.MetricsUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Sentinel 的监控指标
 * 之所以实现ApplicationListener<ApplicationReadyEvent>，是为了确保指标器在整个SpringBoot容器启动完成后才启动
 *
 * @author : Eric Lu
 * @version :
 * @date : 2021-12-15 10:08
 * @since :
 **/
public class SentinelMetrics implements MetricExtension, ApplicationListener<ApplicationReadyEvent> {

    /**
     * Prefix used for all Sentinel metric names.
     */
    private static final String PREFIX = "sentinel";

    /**
     * Metric name
     */
    private static final String PASS_REQUESTS_TOTAL 		= PREFIX + ".pass.requests.total";
    private static final String BLOCK_REQUESTS_TOTAL 	= PREFIX + ".block.requests.total";
    private static final String SUCCESS_REQUESTS_TOTAL 	= PREFIX + ".success.requests.total";
    private static final String EXCEPTION_REQUESTS_TOTAL = PREFIX + ".exception_requests_total";
    private static final String REQUESTS_LATENCY_SECONDS = PREFIX + ".requests.latency.seconds";
    private static final String CURRENT_THREADS 			= PREFIX + ".current.threads";
    private static final String DEFAULT_TAT_NAME 		= "resource";
    private final AtomicLong CURRENT_THREAD_COUNT = new AtomicLong(0);

    private MeterRegistry meterRegistry;

    @Override
    public void addPass(String resource, int n, Object... args) {
        meterRegistry.counter(PASS_REQUESTS_TOTAL, DEFAULT_TAT_NAME, resource).increment(n);
    }

    @Override
    public void addBlock(String resource, int n, String origin, BlockException ex, Object... args) {
        meterRegistry.counter(
                BLOCK_REQUESTS_TOTAL,
                resource, ex.getClass().getSimpleName(),
                ex.getRuleLimitApp(), origin
        ).increment(n);
    }

    @Override
    public void addSuccess(String resource, int n, Object... args) {
        meterRegistry.counter(SUCCESS_REQUESTS_TOTAL, DEFAULT_TAT_NAME, resource).increment(n);
    }

    @Override
    public void addException(String resource, int n, Throwable throwable) {
        meterRegistry.counter(EXCEPTION_REQUESTS_TOTAL, DEFAULT_TAT_NAME, resource).increment(n);
    }

    @Override
    public void addRt(String resource, long rt, Object... args) {
        meterRegistry.timer(REQUESTS_LATENCY_SECONDS, DEFAULT_TAT_NAME, resource).record(rt, TimeUnit.MICROSECONDS);
    }

    @Override
    public void increaseThreadNum(String resource, Object... args) {
        Tags tags = Tags.of(DEFAULT_TAT_NAME, resource);
        meterRegistry.gauge(
                CURRENT_THREADS,
                tags,
                CURRENT_THREAD_COUNT, AtomicLong::incrementAndGet
        );
    }

    @Override
    public void decreaseThreadNum(String resource, Object... args) {
        Tags tags = Tags.of(DEFAULT_TAT_NAME, resource);
        meterRegistry.gauge(
                CURRENT_THREADS,
                tags,
                CURRENT_THREAD_COUNT, AtomicLong::decrementAndGet
        );
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ConfigurableApplicationContext applicationContext = event.getApplicationContext();
        this.meterRegistry = MetricsUtils.determinedMeterRegistry(applicationContext);
    }

}
