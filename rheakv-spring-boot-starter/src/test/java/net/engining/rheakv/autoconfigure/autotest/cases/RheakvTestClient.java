package net.engining.rheakv.autoconfigure.autotest.cases;

import com.alipay.sofa.jraft.rhea.client.RheaIterator;
import com.alipay.sofa.jraft.rhea.client.RheaKVStore;
import com.alipay.sofa.jraft.rhea.storage.KVEntry;
import com.alipay.sofa.jraft.rhea.util.Lists;
import net.engining.pg.storage.rheakv.KvClient;
import net.engining.rheakv.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.alipay.sofa.jraft.util.BytesUtil.readUtf8;
import static com.alipay.sofa.jraft.util.BytesUtil.writeUtf8;

@ActiveProfiles(profiles = {
        "rheakv.client.default",
        "rheakv.client.common",
})
public class RheakvTestClient extends AbstractTestCaseTemplate {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(RheakvTestClient.class);

    @Autowired
    KvClient kvClient;

    @Override
    public void initTestData() throws Exception {

    }

    @Override
    public void assertResult() throws Exception {

    }

    @Override
    public void testProcess() throws Exception {
        RheaKVStore rheaKVStore = kvClient.getRheaKVStore();
        iterator(rheaKVStore);
    }

    @Override
    public void end() throws Exception {

    }

    public void iterator(final RheaKVStore rheaKVStore) {
        final List<byte[]> keys = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            final byte[] bytes = writeUtf8("iterator_demo_" + i);
            keys.add(bytes);
            rheaKVStore.bPut(bytes, bytes);
        }

        final byte[] firstKey = keys.get(0);
        final byte[] lastKey = keys.get(keys.size() - 1);
        final String firstKeyString = readUtf8(firstKey);
        final String lastKeyString = readUtf8(lastKey);

        final RheaIterator<KVEntry> it1 = rheaKVStore.iterator(firstKey, lastKey, 5);
        final RheaIterator<KVEntry> it2 = rheaKVStore.iterator(firstKey, lastKey, 6, false);
        final RheaIterator<KVEntry> it3 = rheaKVStore.iterator(firstKeyString, lastKeyString, 5);
        final RheaIterator<KVEntry> it4 = rheaKVStore.iterator(firstKeyString, lastKeyString, 6, false);

        for (final RheaIterator<KVEntry> it : new RheaIterator[] { it1, it2, it3, it4 }) {
            while (it.hasNext()) {
                final KVEntry kv = it.next();
                LOGGER.info("Sync iterator: key={}, value={}", readUtf8(kv.getKey()), readUtf8(kv.getValue()));
            }
        }
    }
}
