package net.engining.datasource.autoconfigure.autotest.sharding.cases;

import net.engining.datasource.autoconfigure.autotest.sharding.support.AbstractTestCaseTemplate;
import net.engining.datasource.autoconfigure.autotest.sharding.support.DbService;
import net.engining.datasource.autoconfigure.autotest.sharding.support.TOrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

/**
 * @author Eric Lu
 * @date 2019-09-21 23:58
 **/
@ActiveProfiles(profiles={
        "shardingsphere.enable",
        "db.common",
        "db.sharding.common",
        "db.sharding.hikari.h2"
})
public class SimpleShardingTestCase extends AbstractTestCaseTemplate {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleShardingTestCase.class);

    @Autowired
    DbService dbService;

    @Override
    public void initTestData() throws Exception {

    }

    @Override
    public void assertResult() throws Exception {

    }

    @Override
    public void testProcess() throws Exception {
        List<Long> ids = dbService.dsTest4sharding();
        LOGGER.debug("get primery keys for datasource default: {}", ids);
        this.testAssertDataContext.put("primerykeys4default", ids);

    }

    @Override
    public void end() throws Exception {

    }
}
