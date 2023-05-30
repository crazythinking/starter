package net.engining.mongo.autoconfigure.autotest.cases;

import net.engining.mongo.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import net.engining.mongo.autoconfigure.autotest.support.KvLob;
import net.engining.mongo.autoconfigure.autotest.support.KvLobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2020-07-03 17:15
 * @since :
 **/
@ActiveProfiles({
        "mongo.test"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class MongoTestCase extends AbstractTestCaseTemplate {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoTestCase.class);

    @Autowired
    private KvLobRepository kvLobRepository;

    @Override
    public void initTestData() throws Exception {

    }

    @Override
    public void assertResult() throws Exception {
    }

    @Override
    public void testProcess() throws Exception {
        KvLob kvLob = new KvLob();
        kvLob.setJsonValue("test");

        kvLob = kvLobRepository.save(kvLob);
        LOGGER.debug("ctLob:{}", kvLob);
        String id = kvLob.getId();
        kvLobRepository.findById(id).ifPresent(kvLob1 -> LOGGER.debug("ctLob1:{}", kvLob1));

        KvLob kvLob2 = new KvLob("100", "test2");
        kvLob2 = kvLobRepository.save(kvLob2);
        LOGGER.debug("ctLob2:{}", kvLob2);
    }

    @Override
    public void end() throws Exception {

    }
}
