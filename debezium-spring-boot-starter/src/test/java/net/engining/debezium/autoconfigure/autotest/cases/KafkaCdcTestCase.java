package net.engining.debezium.autoconfigure.autotest.cases;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.search.Search;
import net.engining.debezium.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import net.engining.pg.support.core.context.ApplicationContextHolder;
import org.assertj.core.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Eric Lu
 **/
@ActiveProfiles(profiles = {
        "debezium.func.catchcdc",
})
public class KafkaCdcTestCase extends AbstractTestCaseTemplate {

    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaCdcTestCase.class);

    @Autowired
    private CompositeMeterRegistry compositeMeterRegistry;

    @Autowired
    private List<MeterRegistry> meterRegistries;


    @Override
    public void initTestData() throws Exception {
    }

    @Override
    public void assertResult() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (Metrics.globalRegistry.getRegistries().equals(compositeMeterRegistry.getRegistries())) {
            LOGGER.warn("CompositeMeterRegistry is same");
        }

        meterRegistries.forEach(meterRegistry -> {
            LOGGER.warn(
                    "the meter registry[{}] number of total meters: {} ",
                    meterRegistry.getClass().getName(),
                    Search.in(meterRegistry).meters().size());
        });

        Assertions.assertThatThrownBy(() -> ApplicationContextHolder.getBean("debeziumAutoConfiguration"))
                .isInstanceOf(BeansException.class);

        Assertions.assertThatThrownBy(() -> ApplicationContextHolder.getBean("healthAutoConfiguration"))
                .isInstanceOf(BeansException.class);

        Assertions.assertThatThrownBy(() -> ApplicationContextHolder.getBean("jRaftNodeAutoConfiguration"))
                .isInstanceOf(BeansException.class);

        Assertions.assertThatThrownBy(() -> ApplicationContextHolder.getBean("debeziumMySqlMetrics"))
                .isInstanceOf(BeansException.class);

        Assertions.assertThatThrownBy(() -> ApplicationContextHolder.getBean("debeziumCompositeHealthContributor"))
                .isInstanceOf(BeansException.class);

    }

    @Override
    public void testProcess() throws Exception {
        TimeUnit.SECONDS.sleep(60);
    }

    @Override
    public void end() {

    }

}
