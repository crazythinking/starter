package net.engining.metrics.debezium;

import com.google.common.collect.Maps;
import io.debezium.connector.mysql.MySqlSnapshotChangeEventSourceMetricsMXBean;
import io.debezium.connector.mysql.MySqlStreamingChangeEventSourceMetricsMXBean;
import io.debezium.pipeline.metrics.traits.ConnectionMetricsMXBean;
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
 * Debezium Mysql 相关的监控指标；
 * 之所以实现ApplicationListener<ApplicationReadyEvent>，是为了确保指标器在整个SpringBoot容器启动完成后才启动
 *
 * @author : Eric Lu
 * @version :
 * @date : 2021-12-15 12:53
 * @since :
 **/
public class DebeziumMySqlMetrics implements DebeziumMetrics, ApplicationListener<ApplicationReadyEvent> {

    private static final String DB_NAME = "mysql";
    /**
     * MySql CDC metrics
     */
    private static final String MYSQL_PREFIX = PREFIX + "."+DB_NAME;

    private MeterRegistry meterRegistry;

    @Autowired
    private DebeziumMetricsProperties debeziumMetricsProperties;

    private final Map<String, ConnectionMetricsMXBean> connectionMXBeanMap = Maps.newHashMap();
    private final Map<String, MySqlStreamingChangeEventSourceMetricsMXBean> streamingMXBeanMap = Maps.newHashMap();
    private final Map<String, MySqlSnapshotChangeEventSourceMetricsMXBean> snapshotMXBeanMap = Maps.newHashMap();
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
        List<String> cdcNames = debeziumMetricsProperties.getCdcDefinition().get(DbType.MySQL);

        if (ValidateUtilExt.isNotNullOrEmpty(cdcNames)){
            for (String cdcName : cdcNames) {
                ConnectionMetricsMXBean connectionMXBean = findConnectionMXBean(DB_NAME, cdcName);
                if (ValidateUtilExt.isNotNullOrEmpty(connectionMXBean)) {
                    connectionMXBeanMap.put(cdcName, connectionMXBean);
                }

                MySqlStreamingChangeEventSourceMetricsMXBean streamingMXBean = findStreamingMXBean(cdcName);
                if (ValidateUtilExt.isNotNullOrEmpty(streamingMXBean)) {
                    streamingMXBeanMap.put(cdcName, streamingMXBean);
                }

                MySqlSnapshotChangeEventSourceMetricsMXBean snapshotMXBean = findSnapshotMXBean(cdcName);
                if (ValidateUtilExt.isNotNullOrEmpty(snapshotMXBean)) {
                    snapshotMXBeanMap.put(cdcName, snapshotMXBean);
                }

                DatabaseHistoryMXBean historyMXBean = findHistoryMXBean(DB_NAME, cdcName);
                if (ValidateUtilExt.isNotNullOrEmpty(historyMXBean)) {
                    historyMXBeanMap.put(cdcName, historyMXBean);
                }
            }

            //注册指标
            for (Map.Entry<String, ConnectionMetricsMXBean> entry : connectionMXBeanMap.entrySet()) {
                connectionMXBeanMetrics(MYSQL_PREFIX, meterRegistry, entry.getKey(), entry.getValue());
            }
            for (Map.Entry<String, MySqlStreamingChangeEventSourceMetricsMXBean> entry : streamingMXBeanMap.entrySet()) {
                streamingMXBeanMetrics(MYSQL_PREFIX, meterRegistry, entry.getKey(), entry.getValue());
            }
            for (Map.Entry<String, MySqlSnapshotChangeEventSourceMetricsMXBean> entry : snapshotMXBeanMap.entrySet()) {
                snapshotMXBeanMetrics(MYSQL_PREFIX, meterRegistry, entry.getKey(), entry.getValue());
            }
            for (Map.Entry<String, DatabaseHistoryMXBean> entry : historyMXBeanMap.entrySet()) {
                historyMXBeanMetrics(MYSQL_PREFIX, meterRegistry, entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 从MBeanServer获取对应的StreamingMXBean
     *
     * @param cdcName       CDC Name，逻辑名称需要与database.server.name配置项一致
     * @return  MySqlStreamingChangeEventSourceMetricsMXBean
     */
    private MySqlStreamingChangeEventSourceMetricsMXBean findStreamingMXBean(String cdcName) {
        ObjectName objectName = DebeziumMetrics.convertObjectName4Stream(DB_NAME, cdcName);

        return JMX.newMBeanProxy(
                MBEAN_SERVER, objectName, MySqlStreamingChangeEventSourceMetricsMXBean.class
        );
    }

    /**
     * 从MBeanServer获取对应的SnapshotMXBean
     *
     * @param cdcName   CDC Name，逻辑名称需要与database.server.name配置项一致
     * @return  MySqlSnapshotChangeEventSourceMetricsMXBean
     */
    private MySqlSnapshotChangeEventSourceMetricsMXBean findSnapshotMXBean(String cdcName) {
        ObjectName objectName = DebeziumMetrics.convertObjectName4Snapshot(DB_NAME, cdcName);

        return JMX.newMBeanProxy(
                MBEAN_SERVER, objectName, MySqlSnapshotChangeEventSourceMetricsMXBean.class
        );
    }


}
