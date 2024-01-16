package net.engining.metrics.debezium;

import cn.hutool.core.util.StrUtil;
import io.debezium.pipeline.metrics.SnapshotChangeEventSourceMetricsMXBean;
import io.debezium.pipeline.metrics.StreamingChangeEventSourceMetricsMXBean;
import io.debezium.relational.history.DatabaseHistoryMXBean;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.BaseUnits;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * @author Eric Lu
 * @date 2022-08-31 14:40
 **/
public interface DebeziumMetrics {

    /**
     * Prefix used for all Debezium metric names.
     */
    String PREFIX = "debezium";

    String STREAM_MBEAN_NAME_PATTERN = "debezium.{}:type=connector-metrics,context=streaming,server={}";
    String SNAPSHOT_MBEAN_NAME_PATTERN = "debezium.{}:type=connector-metrics,context=snapshot,server={}";
    String SCHEMA_HISTORY_MBEAN_NAME_PATTERN = "debezium.{}:type=connector-metrics,context=schema-history,server={}";

    MBeanServer MBEAN_SERVER = ManagementFactory.getPlatformMBeanServer();

    /**
     * Common metrics
     */
    String CONNECTED = "{}.connected";
    String MILLI_SECONDS_SINCE_LAST_EVENT = "{}.milli.seconds.since.last.event";
    String TOTAL_NUMBER_OF_EVENTS_SEEN = "{}.total.number.of.events.seen";
    String NUMBER_OF_EVENTS_FILTERED = "{}.number.of.events.filtered";
    String MAX_QUEUE_SIZE_IN_BYTES = "{}.max.queue.size.in.bytes";
    String CURRENT_QUEUE_SIZE_IN_BYTES = "{}.current.queue.size.in.bytes";
    /**
     * Snapshot metrics
     */
    String SNAPSHOT_DURATION_IN_SECONDS = "{}.snapshot.duration.in.seconds";
    /**
     * Schema history metrics
     */
    String HISTORY_CHANGES_RECOVERED = "{}.history.changes.recovered";
    String HISTORY_CHANGES_APPLIED = "{}.history.changes.applied";

    void registerMXBean();

    static ObjectName convertObjectName4Stream(String dbName, String cdcName) {
        ObjectName objectName;
        try {
            objectName = new ObjectName(StrUtil.format(STREAM_MBEAN_NAME_PATTERN, dbName, cdcName));
        } catch (MalformedObjectNameException e) {
            throw new RuntimeException(e);
        }
        return objectName;
    }

    static ObjectName convertObjectName4Snapshot(String dbName, String cdcName) {
        ObjectName objectName;
        try {
            objectName = new ObjectName(StrUtil.format(SNAPSHOT_MBEAN_NAME_PATTERN, dbName, cdcName));
        } catch (MalformedObjectNameException e) {
            throw new RuntimeException(e);
        }
        return objectName;
    }

    static ObjectName convertObjectName4SchemaHistory(String dbName, String cdcName) {
        ObjectName objectName;
        try {
            objectName = new ObjectName(StrUtil.format(SCHEMA_HISTORY_MBEAN_NAME_PATTERN, dbName, cdcName));
        } catch (MalformedObjectNameException e) {
            throw new RuntimeException(e);
        }
        return objectName;
    }

    /**
     * 从MBeanServer获取对应的HistoryMXBean
     *
     * @param dbName  数据库类型，如mysql，oracle等
     * @param cdcName CDC Name，逻辑名称需要与database.server.name配置项一致
     * @return DatabaseHistoryMXBean
     */
    default DatabaseHistoryMXBean findHistoryMXBean(String dbName, String cdcName) {
        ObjectName objectName = DebeziumMetrics.convertObjectName4SchemaHistory(dbName, cdcName);

        return JMX.newMBeanProxy(
                MBEAN_SERVER, objectName, DatabaseHistoryMXBean.class
        );
    }

    default void historyMXBeanMetrics(String dbPrefix, MeterRegistry meterRegistry,
                                      String cdcName, DatabaseHistoryMXBean historyMXBean) {
        Gauge.builder(
                        StrUtil.format(HISTORY_CHANGES_RECOVERED, dbPrefix),
                        historyMXBean,
                        DatabaseHistoryMXBean::getChangesRecovered
                )
                .description("The number of changes that were read during recovery phase.")
                //cdc connector name
                .tag("cdcName", cdcName)
                .baseUnit(BaseUnits.EVENTS)
                .register(meterRegistry);

        Gauge.builder(
                        StrUtil.format(HISTORY_CHANGES_APPLIED, dbPrefix),
                        historyMXBean,
                        DatabaseHistoryMXBean::getChangesApplied
                )
                .description("the total number of schema changes applied during recovery and runtime.")
                //cdc connector name
                .tag("cdcName", cdcName)
                .baseUnit(BaseUnits.EVENTS)
                .register(meterRegistry);
    }

    default void snapshotMXBeanMetrics(String dbPrefix, MeterRegistry meterRegistry,
                                       String cdcName, SnapshotChangeEventSourceMetricsMXBean snapshotMXBean) {
        Gauge.builder(
                        StrUtil.format(MILLI_SECONDS_SINCE_LAST_EVENT, dbPrefix),
                        snapshotMXBean,
                        SnapshotChangeEventSourceMetricsMXBean::getMilliSecondsSinceLastEvent
                )
                .description("The number of milliseconds since the connector has read and processed the most recent event.")
                //cdc connector name
                .tag("cdcName", cdcName)
                .tag("metricTypes", "snapshot")
                .baseUnit(BaseUnits.MILLISECONDS)
                .register(meterRegistry);

        Gauge.builder(
                        StrUtil.format(TOTAL_NUMBER_OF_EVENTS_SEEN, dbPrefix),
                        snapshotMXBean,
                        SnapshotChangeEventSourceMetricsMXBean::getTotalNumberOfEventsSeen
                )
                .description("The total number of events, since the last start or metrics reset.")
                //cdc connector name
                .tag("cdcName", cdcName)
                .tag("metricTypes", "snapshot")
                .baseUnit(BaseUnits.EVENTS)
                .register(meterRegistry);

        Gauge.builder(
                        StrUtil.format(NUMBER_OF_EVENTS_FILTERED, dbPrefix),
                        snapshotMXBean,
                        SnapshotChangeEventSourceMetricsMXBean::getNumberOfEventsFiltered
                )
                .description("The number of events that have been filtered by include/exclude.")
                //cdc connector name
                .tag("cdcName", cdcName)
                .tag("metricTypes", "snapshot")
                .baseUnit(BaseUnits.EVENTS)
                .register(meterRegistry);

        Gauge.builder(
                        StrUtil.format(MAX_QUEUE_SIZE_IN_BYTES, dbPrefix),
                        snapshotMXBean,
                        SnapshotChangeEventSourceMetricsMXBean::getMaxQueueSizeInBytes
                )
                .description("The maximum buffer of the queue in bytes.")
                //cdc connector name
                .tag("cdcName", cdcName)
                .tag("metricTypes", "snapshot")
                .baseUnit(BaseUnits.BYTES)
                .register(meterRegistry);

        Gauge.builder(
                        StrUtil.format(CURRENT_QUEUE_SIZE_IN_BYTES, dbPrefix),
                        snapshotMXBean,
                        SnapshotChangeEventSourceMetricsMXBean::getCurrentQueueSizeInBytes
                )
                .description("The current volume, in bytes, of records in the queue.")
                //cdc connector name
                .tag("cdcName", cdcName)
                .tag("metricTypes", "snapshot")
                .baseUnit(BaseUnits.BYTES)
                .register(meterRegistry);

        Gauge.builder(
                        StrUtil.format(SNAPSHOT_DURATION_IN_SECONDS, dbPrefix),
                        snapshotMXBean,
                        value -> value.getSnapshotDurationInSeconds() * 1000
                )
                .description("The total number of seconds that the snapshot has taken so far.")
                //cdc connector name
                .tag("cdcName", cdcName)
                .baseUnit(BaseUnits.MILLISECONDS)
                .register(meterRegistry);
    }

    default void streamingMXBeanMetrics(String dbPrefix, MeterRegistry meterRegistry,
                                        String cdcName, StreamingChangeEventSourceMetricsMXBean streamingMXBean) {
        Gauge.builder(
                        StrUtil.format(CONNECTED, dbPrefix),
                        streamingMXBean,
                        value -> {
                            if (value.isConnected()) {
                                return 1;
                            }
                            return 0;
                        }
                )
                .description("Whether the connector is connected to the database.")
                //cdc connector name
                .tag("cdcName", cdcName)
                .tag("metricTypes", "streaming")
                .register(meterRegistry);

        Gauge.builder(
                        StrUtil.format(MILLI_SECONDS_SINCE_LAST_EVENT, dbPrefix),
                        streamingMXBean,
                        StreamingChangeEventSourceMetricsMXBean::getMilliSecondsSinceLastEvent
                )
                .description("The number of milliseconds since the connector has read and processed the most recent event.")
                //cdc connector name
                .tag("cdcName", cdcName)
                .tag("metricTypes", "streaming")
                .baseUnit(BaseUnits.BYTES)
                .register(meterRegistry);

        Gauge.builder(
                        StrUtil.format(TOTAL_NUMBER_OF_EVENTS_SEEN, dbPrefix),
                        streamingMXBean,
                        StreamingChangeEventSourceMetricsMXBean::getTotalNumberOfEventsSeen
                )
                .description("The total number of events, since the last start or metrics reset.")
                //cdc connector name
                .tag("cdcName", cdcName)
                .tag("metricTypes", "streaming")
                .baseUnit(BaseUnits.EVENTS)
                .register(meterRegistry);

        Gauge.builder(
                        StrUtil.format(NUMBER_OF_EVENTS_FILTERED, dbPrefix),
                        streamingMXBean,
                        StreamingChangeEventSourceMetricsMXBean::getNumberOfEventsFiltered
                )
                .description("The number of events that have been filtered by include/exclude.")
                //cdc connector name
                .tag("cdcName", cdcName)
                .tag("metricTypes", "streaming")
                .baseUnit(BaseUnits.EVENTS)
                .register(meterRegistry);

        Gauge.builder(
                        StrUtil.format(MAX_QUEUE_SIZE_IN_BYTES, dbPrefix),
                        streamingMXBean,
                        StreamingChangeEventSourceMetricsMXBean::getMaxQueueSizeInBytes
                )
                .description("The maximum buffer of the queue in bytes.")
                //cdc connector name
                .tag("cdcName", cdcName)
                .tag("metricTypes", "streaming")
                .baseUnit(BaseUnits.BYTES)
                .register(meterRegistry);

        Gauge.builder(
                        StrUtil.format(CURRENT_QUEUE_SIZE_IN_BYTES, dbPrefix),
                        streamingMXBean,
                        StreamingChangeEventSourceMetricsMXBean::getCurrentQueueSizeInBytes
                )
                .description("The current volume, in bytes, of records in the queue.")
                //cdc connector name
                .tag("cdcName", cdcName)
                .tag("metricTypes", "streaming")
                .baseUnit(BaseUnits.BYTES)
                .register(meterRegistry);
    }

}
