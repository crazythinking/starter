package net.engining.debezium.autoconfigure.actuator;

import io.debezium.pipeline.metrics.traits.ConnectionMetricsMXBean;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2022-09-05 13:12
 * @since :
 **/
public abstract class AbstractDebeziumHealthIndicator implements DebeziumHealthIndicator{

    ConnectionMetricsMXBean mxBean;

    abstract ConnectionMetricsMXBean getConnectionMetricsMxBean();
}
