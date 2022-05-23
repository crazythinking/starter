package net.engining.debezium.autoconfigure.autotest.cases;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.search.Search;
import net.engining.debezium.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import net.engining.debezium.facility.DebeziumServerBootstrap;
import net.engining.pg.storage.rheakv.KvServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static net.engining.debezium.autoconfigure.DebeziumAutoConfiguration.DEBEZIUM_SERVER_BOOTSTRAP_MAP;

/**
 * @author Eric Lu
 **/
@ActiveProfiles(profiles = {
        "debezium.common",
        "debezium.xxljob.mysql",
        "debezium.demods0.mysql",
        //"debezium.xxljob.oracle",
        "rheakv.server.default",
        "rheakv.server1.common"
})
public class JRaftTestCase extends AbstractTestCaseTemplate {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(JRaftTestCase.class);

    @Autowired
    Environment environment;

    @Autowired
    KvServer kvServer;

    @Autowired
    private List<MeterRegistry> meterRegistries;

    @Autowired
    @Qualifier(DEBEZIUM_SERVER_BOOTSTRAP_MAP)
    Map<String, DebeziumServerBootstrap> bootstrapMap;

    @Override
    public void initTestData() throws Exception {

        TimeUnit.SECONDS.sleep(60);
        //CDC stop
        bootstrapMap.get("demods0-mysql").stop();
    }

    @Override
    public void assertResult() {
        meterRegistries.forEach(meterRegistry -> {
            LOGGER.warn(
                    "the meter registry[{}] number of total meters: {} ",
                    meterRegistry.getClass().getName(),
                    Search.in(meterRegistry).meters().size());
        });
    }

    @Override
    public void testProcess() throws Exception {

        TimeUnit.SECONDS.sleep(60);
    }

    @Override
    public void end() {
    }

}
