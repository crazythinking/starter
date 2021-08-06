package net.engining.datasource.autoconfigure.autotest.jdbc.cases;

import cn.hutool.core.util.RandomUtil;
import net.engining.datasource.autoconfigure.autotest.jdbc.support.AbstractTestCaseTemplate;
import net.engining.datasource.autoconfigure.autotest.support.OperationLogService;
import net.engining.gm.entity.dto.OperAdtLogDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.Assert;

/**
 * @author Eric Lu
 * @date 2019-09-21 23:58
 **/
@ActiveProfiles(profiles={
        "autotest.hikari",
        "db.common",
		"hikari.clickhouse",
        //"hikari.h2"
})
public class SimpleTestCase extends AbstractTestCaseTemplate {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTestCase.class);

    @Autowired
    OperationLogService operationLogService;

    @Override
    public void initTestData() throws Exception {

    }

    @Override
    public void assertResult() throws Exception {
        OperAdtLogDto operAdtLog = operationLogService.fetch((Integer) this.testAssertDataContext.get("insertKey4default"));
        Assert.notNull(operAdtLog, "has record by default datasource");
    }

    @Override
    public void testProcess() throws Exception {
        int id = RandomUtil.randomInt();
        operationLogService.dsTest(id);
        this.testAssertDataContext.put("insertKey4default", id);

    }


    @Override
    public void end() throws Exception {

    }
}
