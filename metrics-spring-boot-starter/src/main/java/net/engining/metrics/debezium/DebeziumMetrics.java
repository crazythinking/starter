package net.engining.metrics.debezium;

import cn.hutool.core.util.StrUtil;
import io.debezium.relational.history.DatabaseHistoryMXBean;

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

    String STREAM_MBEAN_NAME_PATTERN="debezium.{}:type=connector-metrics,context=streaming,server={}";
    String SNAPSHOT_MBEAN_NAME_PATTERN="debezium.{}:type=connector-metrics,context=snapshot,server={}";
    String SCHEMA_HISTORY_MBEAN_NAME_PATTERN="debezium.{}:type=connector-metrics,context=schema-history,server={}";

    MBeanServer MBEAN_SERVER = ManagementFactory.getPlatformMBeanServer();

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
     * @param dbName    数据库类型，如mysql，oracle等
     * @param cdcName   CDC Name，逻辑名称需要与database.server.name配置项一致
     * @return  DatabaseHistoryMXBean
     */
    default DatabaseHistoryMXBean findHistoryMXBean(String dbName, String cdcName) {
        ObjectName objectName = DebeziumMetrics.convertObjectName4SchemaHistory(dbName, cdcName);

        return JMX.newMBeanProxy(
                MBEAN_SERVER, objectName, DatabaseHistoryMXBean.class
        );
    }

}
