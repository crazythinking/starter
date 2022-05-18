package net.engining.debezium.autoconfigure.autotest.cases;


import net.engining.debezium.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import net.engining.pg.storage.rheakv.KvServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

import static com.alipay.sofa.jraft.util.BytesUtil.readUtf8;

/**
 * @author Eric Lu
 **/
@ActiveProfiles(profiles = {
        "debezium.common",
        "debezium.xxljob.mysql",
        "debezium.demods0.mysql",
        "rheakv.server3.common"
})
public class JRaftTestServer3 extends AbstractTestCaseTemplate {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(JRaftTestServer3.class);

    @Autowired
    Environment environment;

    @Autowired
    KvServer kvServer;

    @Override
    public void initTestData() throws Exception {
        TimeUnit.SECONDS.sleep(90);
    }

    @Override
    public void assertResult() {
    }

    @Override
    public void testProcess() throws Exception {
        String value0 = readUtf8(kvServer.getRheaKVStore().bGet("xxljob-mysql-1-history"));
        LOGGER.info("kvServer3 xxljob-mysql-1-history: {}", value0);
        TimeUnit.SECONDS.sleep(6000);
    }

    @Override
    public void end() throws Exception {
    }

}
