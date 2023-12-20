package net.engining.metrics.debezium;

import com.google.common.collect.Maps;
import io.debezium.connector.oracle.OracleStreamingChangeEventSourceMetricsMXBean;
import io.debezium.pipeline.metrics.SnapshotChangeEventSourceMetricsMXBean;
import io.debezium.relational.history.DatabaseHistoryMXBean;
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
import java.util.Map;

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

    private static final String DB_NAME = "oracle";
    /**
     * Oracle CDC metrics
     */
    private static final String ORACLE_PREFIX = PREFIX + "." + DB_NAME;

    private MeterRegistry meterRegistry;

    @Autowired
    private DebeziumMetricsProperties debeziumMetricsProperties;

    private final Map<String, OracleStreamingChangeEventSourceMetricsMXBean> streamingMXBeanMap = Maps.newHashMap();
    private final Map<String, SnapshotChangeEventSourceMetricsMXBean> snapshotMXBeanMap = Maps.newHashMap();
    private final Map<String, DatabaseHistoryMXBean> historyMXBeanMap = Maps.newHashMap();

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
                    streamingMXBeanMap.put(cdcName, streamingMXBean);
                }

                SnapshotChangeEventSourceMetricsMXBean snapshotMXBean = findSnapshotMXBean(cdcName);
                if (ValidateUtilExt.isNotNullOrEmpty(snapshotMXBean)) {
                    snapshotMXBeanMap.put(cdcName, snapshotMXBean);
                }

                DatabaseHistoryMXBean historyMXBean = findHistoryMXBean(DB_NAME, cdcName);
                if (ValidateUtilExt.isNotNullOrEmpty(historyMXBean)) {
                    historyMXBeanMap.put(cdcName, historyMXBean);
                }
            }
        }

        //注册指标
        for (Map.Entry<String, OracleStreamingChangeEventSourceMetricsMXBean> entry : streamingMXBeanMap.entrySet()) {
            streamingMXBeanMetrics(ORACLE_PREFIX, meterRegistry, entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, SnapshotChangeEventSourceMetricsMXBean> entry : snapshotMXBeanMap.entrySet()) {
            snapshotMXBeanMetrics(ORACLE_PREFIX, meterRegistry, entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, DatabaseHistoryMXBean> entry : historyMXBeanMap.entrySet()) {
            historyMXBeanMetrics(ORACLE_PREFIX, meterRegistry, entry.getKey(), entry.getValue());
        }
    }

    /**
     * 从MBeanServer获取对应的StreamingMXBean
     *
     * @param cdcName CDC Name，逻辑名称需要与database.server.name配置项一致
     * @return OracleStreamingChangeEventSourceMetricsMXBean
     */
    public OracleStreamingChangeEventSourceMetricsMXBean findStreamingMXBean(String cdcName) {
        ObjectName objectName = DebeziumMetrics.convertObjectName4Stream(DB_NAME, cdcName);

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
        ObjectName objectName = DebeziumMetrics.convertObjectName4Snapshot(DB_NAME, cdcName);

        return JMX.newMBeanProxy(
                MBEAN_SERVER, objectName, SnapshotChangeEventSourceMetricsMXBean.class
        );
    }


}
