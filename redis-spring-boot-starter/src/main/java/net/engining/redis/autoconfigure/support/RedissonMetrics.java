package net.engining.redis.autoconfigure.support;

import cn.hutool.core.util.ReflectUtil;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import net.engining.metrics.MetricsUtils;
import org.redisson.Redisson;
import org.redisson.connection.ClientConnectionsEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Queue;

/**
 * @author : Eric Lu
 * @version :
 * @since : 2023-09-06 13:59
 **/
public class RedissonMetrics implements ApplicationListener<ApplicationReadyEvent> {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(RedissonMetrics.class);

    private final static String REDISSON_CONNECTION_PREFIX = "redisson.connections.";
    private final static String TOTAL = REDISSON_CONNECTION_PREFIX+"total";
    private final static String FREE = REDISSON_CONNECTION_PREFIX+"free";
    private final Redisson redisson;

    public RedissonMetrics(Redisson redisson) {
        this.redisson = redisson;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ConfigurableApplicationContext applicationContext = event.getApplicationContext();
        MeterRegistry meterRegistry = MetricsUtils.determinedMeterRegistry(applicationContext);

        this.redisson.getConnectionManager().getEntrySet().forEach(masterSlaveEntry -> {
            LOGGER.trace("{}", masterSlaveEntry);
            String serverAddr
                    = masterSlaveEntry.getClient().getAddr().getHostString() + ":" + masterSlaveEntry.getClient().getAddr().getPort();
            ClientConnectionsEntry clientConnectionsEntry
                    = (ClientConnectionsEntry) ReflectUtil.getFieldValue(masterSlaveEntry, "masterEntry");
            Queue allSubscribeQueue = (Queue) ReflectUtil.getFieldValue(clientConnectionsEntry, "allSubscribeConnections");
            Queue allConnectionsQueue = (Queue) ReflectUtil.getFieldValue(clientConnectionsEntry, "allConnections");
            Queue freeSubscribeQueue = (Queue) ReflectUtil.getFieldValue(clientConnectionsEntry, "freeSubscribeConnections");
            Queue freeConnectionsQueue = (Queue) ReflectUtil.getFieldValue(clientConnectionsEntry, "freeConnections");

            meterRegistry.gauge(
                    TOTAL,
                    Tags.of("server", serverAddr, "type", "normal"),
                    allConnectionsQueue,
                    Queue::size
            );
            meterRegistry.gauge(
                    TOTAL,
                    Tags.of("server", serverAddr, "type", "subscribe"),
                    allSubscribeQueue,
                    Queue::size
            );
            meterRegistry.gauge(
                    FREE,
                    Tags.of("server", serverAddr, "type", "normal"),
                    freeConnectionsQueue,
                    Queue::size
            );
            meterRegistry.gauge(
                    FREE,
                    Tags.of("server", serverAddr, "type", "subscribe"),
                    freeSubscribeQueue,
                    Queue::size
            );

            LOGGER.trace(
                    "allConnectionsQueue:{}, allSubscribeQueue:{}, freeConnectionsQueue:{}, freeSubscribeQueue:{}",
                    allConnectionsQueue.size(),
                    allSubscribeQueue.size(),
                    freeConnectionsQueue.size(),
                    freeSubscribeQueue.size()
            );
        });

    }
}
