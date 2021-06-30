package net.engining.datasource.autoconfigure.autotest.jpa.cases;

import net.engining.datasource.autoconfigure.autotest.jpa.support.AbstractTestCaseTemplate;
import net.engining.datasource.autoconfigure.autotest.jpa.support.DbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author Eric Lu
 * @date 2019-09-21 23:58
 **/
@ActiveProfiles(profiles={
        "autotest.sharding",
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
        //指定当前线程使用的数据库datasource
        //DataSourceContextHolder.setCurrentDataSourceKey("one");
    }

    @Override
    public void assertResult() throws Exception {

    }

    @Override
    public void testProcess() throws Exception {
        List<Long> ids = dbService.dsTest4sharding();
        LOGGER.debug("get primery keys for datasource default: {}", ids);
        Assert.isTrue(ids.size()==3, "records size should be 3");

        Assert.isTrue(dbService.dsTestQuery3().size()==0, "records size should be 0");
    }

    @Override
    public void end() throws Exception {

    }
}
