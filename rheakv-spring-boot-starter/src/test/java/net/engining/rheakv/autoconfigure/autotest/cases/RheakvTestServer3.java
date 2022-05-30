package net.engining.rheakv.autoconfigure.autotest.cases;


import com.alipay.sofa.jraft.rhea.client.RheaKVStore;
import com.alipay.sofa.jraft.rhea.storage.Sequence;
import net.engining.pg.storage.rheakv.KvServer;
import net.engining.rheakv.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Eric Lu
 **/
@ActiveProfiles(profiles = {
        "rheakv.server.default",
        "rheakv.server3.common"
})
public class RheakvTestServer3 extends AbstractTestCaseTemplate {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(RheakvTestServer3.class);

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
        RheaKVStore kvStore = kvServer.getRheaKVStore();
        TimeUnit.SECONDS.sleep(60);
        getSequence(kvStore);

        TimeUnit.SECONDS.sleep(6000);
    }

    @Override
    public void end() throws Exception {
    }

    private void getSequence(final RheaKVStore rheaKVStore) {

        // async
        final CompletableFuture<Sequence> f1 = rheaKVStore.getSequence("sequence", 60);
        final CompletableFuture<Sequence> f2 = rheaKVStore.getSequence("sequence", 70);
        CompletableFuture.allOf(f1, f2).join();
        LOGGER.info("Async getSequence result={}", f1.join());
        LOGGER.info("Async getSequence result={}", f2.join());

        //rheaKVStore.bResetSequence("sequence");
    }

}
