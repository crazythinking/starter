package net.engining.redis.autoconfigure.support;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.redisson.connection.ConnectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于 io.micrometer 实现的 Redisson connection 的指标监控
 *
 * @author : Eric Lu
 * @version :
 * @since : 2023-09-05 17:32
 **/
public class MicrometerConnectionListener implements ConnectionListener {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(MicrometerConnectionListener.class);

    private final static String REDISSON_CONNECTION = "redisson.connection.total";
    private final ConcurrentMap<String, AtomicInteger> connectionCount = new ConcurrentHashMap<>();
    private MeterRegistry meterRegistry;

    public MicrometerConnectionListener(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void onConnect(InetSocketAddress addr) {
        String serverAddr = addr.getHostString() + ":" + addr.getPort();
        if (connectionCount.containsKey(serverAddr)) {
            connectionCount.get(serverAddr).incrementAndGet();
        }
        else {
            connectionCount.putIfAbsent(serverAddr, new AtomicInteger(1));
        }
        meterRegistry.gauge(
                REDISSON_CONNECTION,
                Tags.of("serverAddr", serverAddr),
                connectionCount.get(serverAddr),
                AtomicInteger::get
        );
        LOGGER.trace("Redisson connection {} count: {}", serverAddr, connectionCount.get(serverAddr).get());

    }

    @Override
    public void onDisconnect(InetSocketAddress addr) {
        String serverAddr = addr.getHostString() + ":" + addr.getPort();
        if (connectionCount.containsKey(serverAddr)) {
            connectionCount.get(serverAddr).decrementAndGet();
        }
        meterRegistry.gauge(
                REDISSON_CONNECTION,
                Tags.of("serverAddr", serverAddr),
                connectionCount.get(serverAddr),
                AtomicInteger::get
        );
        LOGGER.trace("Redisson connection {} count: {}", serverAddr, connectionCount.get(serverAddr).get());
    }

}
