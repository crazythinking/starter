package net.engining.metrics.undertow;

import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.binder.BaseUnits;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.undertow.Undertow;
import io.undertow.server.ConnectorStatistics;
import io.undertow.server.session.SessionManagerStatistics;
import net.engining.metrics.MetricsUtils;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServer;
import org.springframework.boot.web.embedded.undertow.UndertowWebServer;
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.ReflectionUtils;
import org.xnio.management.XnioWorkerMXBean;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Undertow 的监控指标；
 * 之所以实现ApplicationListener<ApplicationReadyEvent>，是为了确保指标器在整个SpringBoot容器启动完成后才启动
 *
 * @author : Eric Lu
 * @version :
 * @date : 2021-12-15 12:53
 * @since :
 **/
public class UndertowMetrics implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * Prefix used for all Undertow metric names.
     */
    public static final String PREFIX = "undertow";

    /**
     * XWorker
     */
    private static final String X_WORK_WORKER_POOL_CORE_SIZE = PREFIX + ".xwork.worker.pool.core.size";
    private static final String X_WORK_WORKER_POOL_MAX_SIZE = PREFIX + ".xwork.worker.pool.max.size";
    private static final String X_WORK_WORKER_POOL_SIZE = PREFIX + ".xwork.worker.pool.size";
    private static final String X_WORK_WORKER_THREAD_BUSY_COUNT = PREFIX + ".xwork.worker.thread.busy.count";
    private static final String X_WORK_IO_THREAD_COUNT = PREFIX + ".xwork.io.thread.count";
    private static final String X_WORK_WORKER_QUEUE_SIZE = PREFIX + ".xwork.worker.queue.size";

    /**
     * connectors
     */
    private static final String CONNECTORS_REQUESTS_COUNT = PREFIX + ".connectors.requests.count";
    private static final String CONNECTORS_REQUESTS_ERROR_COUNT = PREFIX + ".connectors.requests.error.count";
    private static final String CONNECTORS_REQUESTS_ACTIVE = PREFIX + ".connectors.requests.active";
    private static final String CONNECTORS_REQUESTS_ACTIVE_MAX = PREFIX + ".connectors.requests.active.max";
    private static final String CONNECTORS_BYTES_SENT = PREFIX + ".connectors.bytes.sent";
    private static final String CONNECTORS_BYTES_RECEIVED = PREFIX + ".connectors.bytes.received";
    private static final String CONNECTORS_PROCESSING_TIME = PREFIX + ".connectors.processing.time";
    private static final String CONNECTORS_PROCESSING_TIME_MAX = PREFIX + ".connectors.processing.time.max";
    private static final String CONNECTORS_CONNECTIONS_ACTIVE = PREFIX + ".connectors.connections.active";
    private static final String CONNECTORS_CONNECTIONS_ACTIVE_MAX = PREFIX + ".connectors.connections.active.max";

    /**
     * session
     */
    private static final String SESSIONS_ACTIVE_MAX = PREFIX + ".sessions.active.max";
    private static final String SESSIONS_ACTIVE_CURRENT = PREFIX + ".sessions.active.current";
    private static final String SESSIONS_CREATED = PREFIX + ".sessions.created";
    private static final String SESSIONS_EXPIRED = PREFIX + ".sessions.expired";
    private static final String SESSIONS_REJECTED = PREFIX + ".sessions.rejected";
    private static final String SESSIONS_ALIVE_MAX = PREFIX + ".sessions.alive.max";

    private static final Field UNDERTOW_FIELD;
    private final Iterable<Tag> tags = Collections.emptyList();

    private MeterRegistry meterRegistry;

    static {
        UNDERTOW_FIELD = ReflectionUtils.findField(UndertowWebServer.class, "undertow");
        Objects.requireNonNull(UNDERTOW_FIELD, "UndertowWebServer class field undertow not exist.");
        ReflectionUtils.makeAccessible(UNDERTOW_FIELD);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ConfigurableApplicationContext applicationContext = event.getApplicationContext();
        this.meterRegistry = MetricsUtils.determinedMeterRegistry(applicationContext);

        // find UndertowWebServer
        UndertowWebServer undertowWebServer = findUndertowWebServer(applicationContext);
        if (undertowWebServer == null) {
            return;
        }
        Undertow undertow = getUndertow(undertowWebServer);

        // xWorker 指标
        XnioWorkerMXBean xWorker = undertow.getWorker().getMXBean();
        registerXWorker(xWorker);

        // 连接信息指标
        List<Undertow.ListenerInfo> listenerInfoList = undertow.getListenerInfo();
        listenerInfoList.forEach(listenerInfo -> registerConnectorStatistics(listenerInfo));

        // 如果是 web 监控，添加 session 指标
        if (undertowWebServer instanceof UndertowServletWebServer) {
            SessionManagerStatistics statistics = ((UndertowServletWebServer) undertowWebServer).getDeploymentManager()
                    .getDeployment()
                    .getSessionManager()
                    .getStatistics();
            registerSessionStatistics(statistics);
        }
    }

    private void registerXWorker(XnioWorkerMXBean workerMXBean) {
        Gauge.builder(X_WORK_WORKER_POOL_CORE_SIZE, workerMXBean, XnioWorkerMXBean::getCoreWorkerPoolSize)
                .description("XWork core worker pool size")
                .tags(tags)
                .tag("name", workerMXBean.getName())
                .register(this.meterRegistry);

        Gauge.builder(X_WORK_WORKER_POOL_MAX_SIZE, workerMXBean, XnioWorkerMXBean::getMaxWorkerPoolSize)
                .description("XWork max worker pool size")
                .tags(tags)
                .tag("name", workerMXBean.getName())
                .register(this.meterRegistry);

        Gauge.builder(X_WORK_WORKER_POOL_SIZE, workerMXBean, XnioWorkerMXBean::getWorkerPoolSize)
                .description("XWork worker pool size")
                .tags(tags)
                .tag("name", workerMXBean.getName())
                .register(this.meterRegistry);

        Gauge.builder(X_WORK_WORKER_THREAD_BUSY_COUNT, workerMXBean, XnioWorkerMXBean::getBusyWorkerThreadCount)
                .description("XWork busy worker thread count")
                .tags(tags)
                .tag("name", workerMXBean.getName())
                .register(this.meterRegistry);

        Gauge.builder(X_WORK_IO_THREAD_COUNT, workerMXBean, XnioWorkerMXBean::getIoThreadCount)
                .description("XWork io thread count")
                .tags(tags)
                .tag("name", workerMXBean.getName())
                .register(this.meterRegistry);

        Gauge.builder(X_WORK_WORKER_QUEUE_SIZE, workerMXBean, XnioWorkerMXBean::getWorkerQueueSize)
                .description("XWork worker queue size")
                .tags(tags)
                .tag("name", workerMXBean.getName())
                .register(this.meterRegistry);
    }

    private void registerConnectorStatistics(Undertow.ListenerInfo listenerInfo) {
        String protocol = listenerInfo.getProtcol();
        ConnectorStatistics statistics = listenerInfo.getConnectorStatistics();
        Gauge.builder(CONNECTORS_REQUESTS_COUNT, statistics, ConnectorStatistics::getRequestCount)
                .tags(tags)
                .tag("protocol", protocol)
                .register(this.meterRegistry);

        Gauge.builder(CONNECTORS_REQUESTS_ERROR_COUNT, statistics, ConnectorStatistics::getErrorCount)
                .tags(tags)
                .tag("protocol", protocol)
                .register(this.meterRegistry);

        Gauge.builder(CONNECTORS_REQUESTS_ACTIVE, statistics, ConnectorStatistics::getActiveRequests)
                .tags(tags)
                .tag("protocol", protocol)
                .baseUnit(BaseUnits.CONNECTIONS)
                .register(this.meterRegistry);

        Gauge.builder(CONNECTORS_REQUESTS_ACTIVE_MAX, statistics, ConnectorStatistics::getMaxActiveRequests)
                .tags(tags)
                .tag("protocol", protocol)
                .baseUnit(BaseUnits.CONNECTIONS)
                .register(this.meterRegistry);

        Gauge.builder(CONNECTORS_BYTES_SENT, statistics, ConnectorStatistics::getBytesSent)
                .tags(tags)
                .tag("protocol", protocol)
                .baseUnit(BaseUnits.BYTES)
                .register(this.meterRegistry);

        Gauge.builder(CONNECTORS_BYTES_RECEIVED, statistics, ConnectorStatistics::getBytesReceived)
                .tags(tags)
                .tag("protocol", protocol)
                .baseUnit(BaseUnits.BYTES)
                .register(this.meterRegistry);

        Gauge.builder(CONNECTORS_PROCESSING_TIME, statistics, (s) -> TimeUnit.NANOSECONDS.toMillis(s.getProcessingTime()))
                .tags(tags)
                .tag("protocol", protocol)
                .baseUnit(BaseUnits.MILLISECONDS)
                .register(this.meterRegistry);

        Gauge.builder(CONNECTORS_PROCESSING_TIME_MAX, statistics, (s) -> TimeUnit.NANOSECONDS.toMillis(s.getMaxProcessingTime()))
                .tags(tags)
                .tag("protocol", protocol)
                .baseUnit(BaseUnits.MILLISECONDS)
                .register(this.meterRegistry);

        Gauge.builder(CONNECTORS_CONNECTIONS_ACTIVE, statistics, ConnectorStatistics::getActiveConnections)
                .tags(tags)
                .tag("protocol", protocol)
                .baseUnit(BaseUnits.CONNECTIONS)
                .register(this.meterRegistry);

        Gauge.builder(CONNECTORS_CONNECTIONS_ACTIVE_MAX, statistics, ConnectorStatistics::getMaxActiveConnections)
                .tags(tags)
                .tag("protocol", protocol)
                .baseUnit(BaseUnits.CONNECTIONS)
                .register(this.meterRegistry);
    }

    private void registerSessionStatistics(SessionManagerStatistics statistics) {
        Gauge.builder(SESSIONS_ACTIVE_MAX, statistics, SessionManagerStatistics::getMaxActiveSessions)
                .tags(tags)
                .baseUnit(BaseUnits.SESSIONS)
                .register(this.meterRegistry);

        Gauge.builder(SESSIONS_ACTIVE_CURRENT, statistics, SessionManagerStatistics::getActiveSessionCount)
                .tags(tags)
                .baseUnit(BaseUnits.SESSIONS)
                .register(this.meterRegistry);

        FunctionCounter.builder(SESSIONS_CREATED, statistics, SessionManagerStatistics::getCreatedSessionCount)
                .tags(tags)
                .baseUnit(BaseUnits.SESSIONS)
                .register(this.meterRegistry);

        FunctionCounter.builder(SESSIONS_EXPIRED, statistics, SessionManagerStatistics::getExpiredSessionCount)
                .tags(tags)
                .baseUnit(BaseUnits.SESSIONS)
                .register(this.meterRegistry);

        FunctionCounter.builder(SESSIONS_REJECTED, statistics, SessionManagerStatistics::getRejectedSessions)
                .tags(tags)
                .baseUnit(BaseUnits.SESSIONS)
                .register(this.meterRegistry);

        TimeGauge.builder(SESSIONS_ALIVE_MAX, statistics, TimeUnit.SECONDS, SessionManagerStatistics::getHighestSessionCount)
                .tags(tags)
                .register(this.meterRegistry);
    }

    private static Undertow getUndertow(UndertowWebServer undertowWebServer) {
        return (Undertow) ReflectionUtils.getField(UNDERTOW_FIELD, undertowWebServer);
    }

    private static UndertowWebServer findUndertowWebServer(ConfigurableApplicationContext applicationContext) {
        WebServer webServer;
        if (applicationContext instanceof ReactiveWebServerApplicationContext) {
            webServer = ((ReactiveWebServerApplicationContext) applicationContext).getWebServer();
        } else if (applicationContext instanceof ServletWebServerApplicationContext) {
            webServer = ((ServletWebServerApplicationContext) applicationContext).getWebServer();
        } else {
            return null;
        }
        if (webServer instanceof UndertowWebServer) {
            return (UndertowWebServer) webServer;
        }
        return null;
    }
}
