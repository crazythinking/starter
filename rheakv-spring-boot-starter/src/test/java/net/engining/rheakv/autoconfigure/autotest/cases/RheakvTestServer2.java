package net.engining.rheakv.autoconfigure.autotest.cases;


import com.alipay.sofa.jraft.rhea.client.RheaKVStore;
import net.engining.pg.storage.rheakv.KvServer;
import net.engining.rheakv.autoconfigure.autotest.support.AbstractTestCaseTemplate;
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
        "rheakv.server.default",
        "rheakv.server2.common"
})
public class RheakvTestServer2 extends AbstractTestCaseTemplate {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(RheakvTestServer2.class);

    @Autowired
    Environment environment;

    @Autowired
    KvServer kvServer;

    @Override
    public void initTestData() throws Exception {
    }

    @Override
    public void assertResult() {
    }

    @Override
    public void testProcess() throws Exception {
        RheaKVStore kvStore = kvServer.getRheaKVStore();
        TimeUnit.SECONDS.sleep(60);
        getValue(kvStore);

        TimeUnit.SECONDS.sleep(6000);
    }

    @Override
    public void end() throws Exception {
    }

    private void getValue(RheaKVStore kvStore) {
        LOGGER.info("Async put 1 {} success.", readUtf8(kvStore.bGet("1")));
        LOGGER.info("Async put 2 {} success.", readUtf8(kvStore.bGet("2")));
    }

}
