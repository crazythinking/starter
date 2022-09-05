package net.engining.debezium.autoconfigure.actuator;

import com.google.common.collect.Maps;
import io.debezium.pipeline.metrics.traits.ConnectionMetricsMXBean;
import net.engining.metrics.debezium.DebeziumMetrics;
import net.engining.metrics.prop.DebeziumMetricsProperties;
import net.engining.pg.support.db.DbType;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.actuate.health.CompositeHealthContributor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.NamedContributor;

import javax.management.JMX;
import javax.management.ObjectName;
import java.util.Iterator;
import java.util.Map;

/**
 * Adapter that converts a map of {@link DebeziumHealthIndicator} into a {@link CompositeHealthContributor}.
 *
 * @author : Eric Lu
 * @version :
 * @date : 2022-09-05 10:59
 * @since :
 **/
public class DebeziumCompositeHealthContributor implements CompositeHealthContributor, InitializingBean {

    private final DebeziumMetricsProperties debeziumMetricsProperties;

    private final Map<String, DebeziumHealthIndicator> indicators = Maps.newHashMap();

    public DebeziumCompositeHealthContributor(DebeziumMetricsProperties debeziumMetricsProperties) {
        this.debeziumMetricsProperties = debeziumMetricsProperties;
    }

    @Override
    public HealthContributor getContributor(String name) {
        return indicators.get(name);
    }

    @NotNull
    @Override
    public Iterator<NamedContributor<HealthContributor>> iterator() {
        return this.indicators.values().stream().map(this::asNamedContributor).iterator();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.debeziumMetricsProperties.getCdcDefinition().forEach((dbType, cdcNames) -> {
            if (dbType.equals(DbType.MySQL)) {
                for (String cdcName : cdcNames) {
                    ConnectionMetricsMXBean mxBean = findConnectionMXBean("mysql", cdcName);
                    popHealthIndicators(cdcName, dbType);
                }
            } else if (dbType.equals(DbType.Oracle)) {
                for (String cdcName : cdcNames) {
                    ConnectionMetricsMXBean mxBean = findConnectionMXBean("oracle", cdcName);
                    popHealthIndicators(cdcName, dbType);
                }
            }

        });
    }

    private void popHealthIndicators(String cdcName, DbType dbType) {
        this.indicators.put(cdcName, new AbstractDebeziumHealthIndicator() {
            @Override
            public String getName() {
                return cdcName;
            }

            @Override
            public DbType getDbType() {
                return dbType;
            }

            @Override
            public ConnectionMetricsMXBean getConnectionMetricsMxBean() {
                return this.mxBean = findConnectionMXBean(dbType.getLabel().toLowerCase(), cdcName);
            }

            @Override
            public Health health() {
                Health health = Health.unknown().build();
                if (ValidateUtilExt.isNullOrEmpty(mxBean)) {
                    //如果mxBean为空，说明之前初始化时还未产生，这里再尝试获取一次
                    getConnectionMetricsMxBean();
                    //还是为空
                    if (ValidateUtilExt.isNullOrEmpty(mxBean)) {
                        return health;
                    }
                }
                else {
                    if (mxBean.isConnected()) {
                        return Health.up().build();
                    }
                    else {
                        return Health.down().build();
                    }
                }

                return health;
            }
        });
    }

    private ConnectionMetricsMXBean findConnectionMXBean(String dbType, String cdcName) {
        ObjectName objectName = DebeziumMetrics.convertObjectName4Stream(dbType, cdcName);

        return JMX.newMBeanProxy(
                DebeziumMetrics.MBEAN_SERVER, objectName, ConnectionMetricsMXBean.class
        );
    }

    private NamedContributor<HealthContributor> asNamedContributor(DebeziumHealthIndicator indicator) {
        return new NamedContributor<HealthContributor>() {

            @Override
            public String getName() {
                return indicator.getName();
            }

            @Override
            public HealthIndicator getContributor() {
                if (indicator != null) {
                    indicator.health();
                }
                return indicator;
            }

        };
    }

}
