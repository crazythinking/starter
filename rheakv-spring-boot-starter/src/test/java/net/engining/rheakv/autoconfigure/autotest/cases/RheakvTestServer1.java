package net.engining.rheakv.autoconfigure.autotest.cases;


import com.alipay.sofa.jraft.rhea.client.FutureHelper;
import com.alipay.sofa.jraft.rhea.client.RheaKVStore;
import com.alipay.sofa.jraft.rhea.storage.KVEntry;
import com.alipay.sofa.jraft.rhea.storage.Sequence;
import com.alipay.sofa.jraft.rhea.util.Lists;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.search.Search;
import net.engining.pg.storage.rheakv.KvServer;
import net.engining.rheakv.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.alipay.sofa.jraft.util.BytesUtil.readUtf8;
import static com.alipay.sofa.jraft.util.BytesUtil.writeUtf8;

/**
 * @author Eric Lu
 **/
@ActiveProfiles(profiles = {
        "rheakv.server.default",
        "rheakv.server1.common",
        "metrics.common"
})
public class RheakvTestServer1 extends AbstractTestCaseTemplate {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(RheakvTestServer1.class);

    @Autowired
    Environment environment;

    @Autowired
    KvServer kvServer;

    @Autowired
    private List<MeterRegistry> meterRegistries;

    @Autowired
    private CompositeMeterRegistry compositeMeterRegistry;

    @Override
    public void initTestData() throws Exception {
        TimeUnit.SECONDS.sleep(60);
    }

    @Override
    public void assertResult() throws Exception {
        if (Metrics.globalRegistry.getRegistries().equals(compositeMeterRegistry.getRegistries())){
            LOGGER.warn("CompositeMeterRegistry is same");
        }

        meterRegistries.forEach(meterRegistry -> {
            LOGGER.warn(
                    "the meter registry[{}] number of total meters: {} ",
                    meterRegistry.getClass().getName(),
                    Search.in(meterRegistry).meters().size());
        });

        TimeUnit.SECONDS.sleep(6000);
    }

    @Override
    public void testProcess() throws Exception {
        RheaKVStore kvStore = kvServer.getRheaKVStore();
        kvStore.bPut("0", writeUtf8("put_example_value"));
        put(kvStore);
        getSequence(kvStore);

    }

    @Override
    public void end() throws Exception {
    }

    private void put(final RheaKVStore rheaKVStore) {
        final byte[] value = writeUtf8("put_example_value");
        final CompletableFuture<Boolean> r1 = rheaKVStore.put("1", value);
        if (FutureHelper.get(r1)) {
            LOGGER.info("Async put 1 {} success.", readUtf8(rheaKVStore.bGet("1")));
        }

        final CompletableFuture<Boolean> r2 = rheaKVStore.put(writeUtf8("2"), value);
        if (FutureHelper.get(r2)) {
            LOGGER.info("Async put 2 {} success.", readUtf8(rheaKVStore.bGet("2")));
        }

        final boolean r3 = rheaKVStore.bPut("3", value);
        if (r3) {
            LOGGER.info("Sync put 3 {} success.", readUtf8(rheaKVStore.bGet("3")));
        }

        final boolean r4 = rheaKVStore.bPut(writeUtf8("4"), value);
        if (r4) {
            LOGGER.info("Sync put 4 {} success.", readUtf8(rheaKVStore.bGet("4")));
        }

        // put list
        final KVEntry kv1 = new KVEntry(writeUtf8("10"), value);
        final KVEntry kv2 = new KVEntry(writeUtf8("11"), value);
        final KVEntry kv3 = new KVEntry(writeUtf8("12"), value);
        final KVEntry kv4 = new KVEntry(writeUtf8("13"), value);
        final KVEntry kv5 = new KVEntry(writeUtf8("14"), value);

        List<KVEntry> entries = Lists.newArrayList(kv1, kv2, kv3);

        final CompletableFuture<Boolean> r5 = rheaKVStore.put(entries);
        if (FutureHelper.get(r5)) {
            for (final KVEntry entry : entries) {
                LOGGER.info("Async put list {} with value {} success.", readUtf8(entry.getKey()),
                        readUtf8(entry.getValue()));
            }
        }

        entries = Lists.newArrayList(kv3, kv4, kv5);
        final boolean r6 = rheaKVStore.bPut(entries);
        if (r6) {
            for (final KVEntry entry : entries) {
                LOGGER.info("Sync put list {} with value {} success.", readUtf8(entry.getKey()),
                        readUtf8(entry.getValue()));
            }
        }
    }

    private void getSequence(final RheaKVStore rheaKVStore) {
        final byte[] key = writeUtf8("sequence");
        rheaKVStore.getSequence(key, 10);

        // async
        final CompletableFuture<Sequence> f1 = rheaKVStore.getSequence(key, 20);
        final CompletableFuture<Sequence> f2 = rheaKVStore.getSequence("sequence", 30);
        CompletableFuture.allOf(f1, f2).join();
        LOGGER.info("Async getSequence result={}", f1.join());
        LOGGER.info("Async getSequence result={}", f2.join());

        final CompletableFuture<Boolean> f3 = rheaKVStore.resetSequence(key);
        f3.join();

        // sync
        final Sequence b1 = rheaKVStore.bGetSequence(key, 40);
        final Sequence b2 = rheaKVStore.bGetSequence("sequence", 50);
        LOGGER.info("Sync getSequence result={}", b1);
        LOGGER.info("Sync getSequence result={}", b2);

        //rheaKVStore.bResetSequence(key);
    }

}
