package net.engining.debezium.autoconfigure.autotest.cases;


import io.debezium.connector.mysql.MySqlStreamingChangeEventSourceMetricsMXBean;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.search.Search;
import net.engining.debezium.autoconfigure.actuator.AbstractDebeziumHealthIndicator;
import net.engining.debezium.autoconfigure.actuator.DebeziumCompositeHealthContributor;
import net.engining.debezium.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import net.engining.metrics.debezium.DebeziumMySqlMetrics;
import net.engining.pg.support.core.context.ApplicationContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Eric Lu
 **/
@ActiveProfiles(profiles = {
        "debezium.common",
        "debezium.xxljob.mysql",
        //"debezium.xxljob.oracle",
})
public class SimpleTestCase extends AbstractTestCaseTemplate {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTestCase.class);

    @Autowired
    private CompositeMeterRegistry compositeMeterRegistry;

    @Autowired
    private List<MeterRegistry> meterRegistries;

    @Autowired
    private DebeziumMySqlMetrics debeziumMySqlMetrics;

    @Override
    public void initTestData() throws Exception {
    }

    @Override
    public void assertResult() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (Metrics.globalRegistry.getRegistries().equals(compositeMeterRegistry.getRegistries())){
            LOGGER.warn("CompositeMeterRegistry is same");
        }

        meterRegistries.forEach(meterRegistry -> {
            LOGGER.warn(
                    "the meter registry[{}] number of total meters: {} ",
                    meterRegistry.getClass().getName(),
                    Search.in(meterRegistry).meters().size());
        });

        //MySqlStreamingChangeEventSourceMetricsMXBean mxBean =
        //        DebeziumMySqlMetrics.findStreamingMXBean("xxljob-mysql");

        Method method = ReflectUtils.findDeclaredMethod(
                DebeziumMySqlMetrics.class, "findStreamingMXBean", new Class[]{String.class}
        );
        method.setAccessible(true);
        MySqlStreamingChangeEventSourceMetricsMXBean mxBean =
                (MySqlStreamingChangeEventSourceMetricsMXBean) method.invoke(debeziumMySqlMetrics, "xxljob-mysql");

        LOGGER.warn("isConnected:{}", mxBean.isConnected());
        LOGGER.warn("getMaxQueueSizeInBytes:{}", mxBean.getMaxQueueSizeInBytes());
        LOGGER.warn("getCurrentQueueSizeInBytes:{}", mxBean.getCurrentQueueSizeInBytes());

        DebeziumCompositeHealthContributor healthContributor =
                ApplicationContextHolder.getBean("debeziumCompositeHealthContributor");

        AbstractDebeziumHealthIndicator indicator = (AbstractDebeziumHealthIndicator) healthContributor.iterator().next().getContributor();
        Health health = indicator.getHealth(true);
        LOGGER.warn("{}:{}", indicator.getName(), health.toString());
    }

    @Override
    public void testProcess() throws Exception {
        TimeUnit.SECONDS.sleep(60);
    }

    @Override
    public void end() {

    }

}
