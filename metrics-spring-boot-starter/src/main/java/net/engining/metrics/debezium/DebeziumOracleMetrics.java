package net.engining.metrics.debezium;

import io.debezium.connector.oracle.OracleStreamingChangeEventSourceMetricsMXBean;
import io.debezium.pipeline.metrics.SnapshotChangeEventSourceMetricsMXBean;
import io.debezium.relational.history.DatabaseHistoryMXBean;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import net.engining.metrics.MetricsUtils;
import net.engining.metrics.prop.DebeziumMetricsProperties;
import net.engining.pg.support.db.DbType;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

import javax.management.JMX;
import javax.management.ObjectName;
import java.util.List;

/**
 * Debezium Oracle 相关的监控指标；
 * 之所以实现ApplicationListener<ApplicationReadyEvent>，是为了确保指标器在整个SpringBoot容器启动完成后才启动
 *
 * @author : Eric Lu
 * @version :
 * @date : 2021-12-15 12:53
 * @since :
 **/
public class DebeziumOracleMetrics implements DebeziumMetrics, ApplicationListener<ApplicationReadyEvent> {

    /**
     * MySql CDC metrics
     */
    public static final String ORACLE_PREFIX = PREFIX + ".oracle";
    /**
     * Common metrics
     */
    private static final String MILLI_SECONDS_SINCE_LAST_EVENT = ORACLE_PREFIX + ".milli.seconds.since.last.event";
    private static final String TOTAL_NUMBER_OF_EVENTS_SEEN = ORACLE_PREFIX + ".total.number.of.events.seen";
    private static final String NUMBER_OF_EVENTS_FILTERED = ORACLE_PREFIX + ".number.of.events.filtered";
    private static final String MAX_QUEUE_SIZE_IN_BYTES = ORACLE_PREFIX + ".max.queue.size.in.bytes";
    private static final String CURRENT_QUEUE_SIZE_IN_BYTES = ORACLE_PREFIX + ".current.queue.size.in.bytes";

    /**
     * Snapshot metrics
     */
    private static final String SNAPSHOT_DURATION_IN_SECONDS = ORACLE_PREFIX + ".snapshot.duration.in.seconds";

    /**
     * Schema history metrics
     */
    private static final String HISTORY_CHANGES_RECOVERED = ORACLE_PREFIX + ".history.changes.recovered";
    private static final String HISTORY_CHANGES_APPLIED = ORACLE_PREFIX + ".history.changes.applied";


    private MeterRegistry meterRegistry;

    @Autowired
    private DebeziumMetricsProperties debeziumMetricsProperties;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ConfigurableApplicationContext applicationContext = event.getApplicationContext();
        this.meterRegistry = MetricsUtils.determinedMeterRegistry(applicationContext);

        registerMXBean();
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void registerMXBean() {
        List<String> cdcNames = debeziumMetricsProperties.getCdcDefinition().get(DbType.Oracle);

        if (ValidateUtilExt.isNotNullOrEmpty(cdcNames)) {
            for (String cdcName : cdcNames) {
                OracleStreamingChangeEventSourceMetricsMXBean streamingMXBean = findStreamingMXBean(cdcName);
                if (ValidateUtilExt.isNotNullOrEmpty(streamingMXBean)) {
                    Gauge.builder(
                                    MILLI_SECONDS_SINCE_LAST_EVENT,
                                    streamingMXBean,
                                    OracleStreamingChangeEventSourceMetricsMXBean::getMilliSecondsSinceLastEvent
                            )
                            .description("The number of milliseconds since the connector has read and processed events.")
                            //cdc connector name
                            .tag("cdcName", cdcName)
                            .tag("metricTypes", "streaming")
                            .register(this.meterRegistry);

                    Gauge.builder(
                                    TOTAL_NUMBER_OF_EVENTS_SEEN,
                                    streamingMXBean,
                                    OracleStreamingChangeEventSourceMetricsMXBean::getTotalNumberOfEventsSeen
                            )
                            .description("The total number of events, since the last start or metrics reset.")
                            //cdc connector name
                            .tag("cdcName", cdcName)
                            .tag("metricTypes", "streaming")
                            .register(this.meterRegistry);

                    Gauge.builder(
                                    NUMBER_OF_EVENTS_FILTERED,
                                    streamingMXBean,
                                    OracleStreamingChangeEventSourceMetricsMXBean::getNumberOfEventsFiltered
                            )
                            .description("The number of events that have been filtered by include/exclude.")
                            //cdc connector name
                            .tag("cdcName", cdcName)
                            .tag("metricTypes", "streaming")
                            .register(this.meterRegistry);

                    Gauge.builder(
                                    MAX_QUEUE_SIZE_IN_BYTES,
                                    streamingMXBean,
                                    OracleStreamingChangeEventSourceMetricsMXBean::getMaxQueueSizeInBytes
                            )
                            .description("The maximum buffer of the queue in bytes.")
                            //cdc connector name
                            .tag("cdcName", cdcName)
                            .tag("metricTypes", "streaming")
                            .register(this.meterRegistry);

                    Gauge.builder(
                                    CURRENT_QUEUE_SIZE_IN_BYTES,
                                    streamingMXBean,
                                    OracleStreamingChangeEventSourceMetricsMXBean::getCurrentQueueSizeInBytes
                            )
                            .description("The current volume, in bytes, of records in the queue.")
                            //cdc connector name
                            .tag("cdcName", cdcName)
                            .tag("metricTypes", "streaming")
                            .register(this.meterRegistry);
                }

                SnapshotChangeEventSourceMetricsMXBean snapshotMXBean = findSnapshotMXBean(cdcName);
                if (ValidateUtilExt.isNotNullOrEmpty(snapshotMXBean)) {
                    Gauge.builder(
                                    MILLI_SECONDS_SINCE_LAST_EVENT,
                                    snapshotMXBean,
                                    SnapshotChangeEventSourceMetricsMXBean::getMilliSecondsSinceLastEvent
                            )
                            .description("The number of milliseconds since the connector has read and processed events.")
                            //cdc connector name
                            .tag("cdcName", cdcName)
                            .tag("metricTypes", "snapshot")
                            .register(this.meterRegistry);

                    Gauge.builder(
                                    TOTAL_NUMBER_OF_EVENTS_SEEN,
                                    snapshotMXBean,
                                    SnapshotChangeEventSourceMetricsMXBean::getTotalNumberOfEventsSeen
                            )
                            .description("The total number of events, since the last start or metrics reset.")
                            //cdc connector name
                            .tag("cdcName", cdcName)
                            .tag("metricTypes", "snapshot")
                            .register(this.meterRegistry);

                    Gauge.builder(
                                    NUMBER_OF_EVENTS_FILTERED,
                                    snapshotMXBean,
                                    SnapshotChangeEventSourceMetricsMXBean::getNumberOfEventsFiltered
                            )
                            .description("The number of events that have been filtered by include/exclude.")
                            //cdc connector name
                            .tag("cdcName", cdcName)
                            .tag("metricTypes", "snapshot")
                            .register(this.meterRegistry);


                    Gauge.builder(
                                    MAX_QUEUE_SIZE_IN_BYTES,
                                    snapshotMXBean,
                                    SnapshotChangeEventSourceMetricsMXBean::getMaxQueueSizeInBytes
                            )
                            .description("The maximum buffer of the queue in bytes.")
                            //cdc connector name
                            .tag("cdcName", cdcName)
                            .tag("metricTypes", "snapshot")
                            .register(this.meterRegistry);

                    Gauge.builder(
                                    CURRENT_QUEUE_SIZE_IN_BYTES,
                                    snapshotMXBean,
                                    SnapshotChangeEventSourceMetricsMXBean::getCurrentQueueSizeInBytes
                            )
                            .description("The current volume, in bytes, of records in the queue.")
                            //cdc connector name
                            .tag("cdcName", cdcName)
                            .tag("metricTypes", "snapshot")
                            .register(this.meterRegistry);

                    Gauge.builder(
                                    SNAPSHOT_DURATION_IN_SECONDS,
                                    snapshotMXBean,
                                    SnapshotChangeEventSourceMetricsMXBean::getSnapshotDurationInSeconds
                            )
                            .description("The total number of seconds that the snapshot has taken so far.")
                            //cdc connector name
                            .tag("cdcName", cdcName)
                            .register(this.meterRegistry);
                }

                DatabaseHistoryMXBean historyMXBean = findHistoryMXBean("mysql", cdcName);
                if (ValidateUtilExt.isNotNullOrEmpty(historyMXBean)) {
                    Gauge.builder(
                                    HISTORY_CHANGES_RECOVERED,
                                    historyMXBean,
                                    DatabaseHistoryMXBean::getChangesRecovered
                            )
                            .description("The number of changes that were read during recovery phase.")
                            //cdc connector name
                            .tag("cdcName", cdcName)
                            .register(this.meterRegistry);

                    Gauge.builder(
                                    HISTORY_CHANGES_APPLIED,
                                    historyMXBean,
                                    DatabaseHistoryMXBean::getChangesApplied
                            )
                            .description("the total number of schema changes applied during recovery and runtime.")
                            //cdc connector name
                            .tag("cdcName", cdcName)
                            .register(this.meterRegistry);
                }
            }
        }
    }

    /**
     * 从MBeanServer获取对应的StreamingMXBean
     *
     * @param cdcName CDC Name，逻辑名称需要与database.server.name配置项一致
     * @return OracleStreamingChangeEventSourceMetricsMXBean
     */
    public OracleStreamingChangeEventSourceMetricsMXBean findStreamingMXBean(String cdcName) {
        ObjectName objectName = DebeziumMetrics.convertObjectName4Stream("oracle", cdcName);

        return JMX.newMBeanProxy(
                MBEAN_SERVER, objectName, OracleStreamingChangeEventSourceMetricsMXBean.class
        );
    }

    /**
     * 从MBeanServer获取对应的SnapshotMXBean
     *
     * @param cdcName CDC Name，逻辑名称需要与database.server.name配置项一致
     * @return SnapshotChangeEventSourceMetricsMXBean
     */
    public SnapshotChangeEventSourceMetricsMXBean findSnapshotMXBean(String cdcName) {
        ObjectName objectName = DebeziumMetrics.convertObjectName4Snapshot("oracle", cdcName);

        return JMX.newMBeanProxy(
                MBEAN_SERVER, objectName, SnapshotChangeEventSourceMetricsMXBean.class
        );
    }


}
