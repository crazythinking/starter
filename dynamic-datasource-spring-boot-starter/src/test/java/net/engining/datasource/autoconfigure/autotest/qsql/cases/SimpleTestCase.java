package net.engining.datasource.autoconfigure.autotest.qsql.cases;

import cn.hutool.core.util.RandomUtil;
import net.engining.datasource.autoconfigure.autotest.qsql.support.AbstractTestCaseTemplate;
import net.engining.datasource.autoconfigure.autotest.support.OperationLogService;
import net.engining.gm.entity.dto.OperAdtLogDto;
import net.engining.pg.support.core.context.DataSourceContextHolder;
import org.assertj.core.util.Lists;
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
        "autotest.hikari",
        "db.common",
		//"hikari.h2.qsql",
        "hikari.clickhouse.qsql"
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
        List<Integer> ids = (List<Integer>) this.testAssertDataContext.get("ids");
        OperAdtLogDto operAdtLogDto = operationLogService.fetch(ids.get(0));
        LOGGER.debug(operAdtLogDto.toString());
    }

    @Override
    public void testProcess() throws Exception {
        Integer id = RandomUtil.randomInt();
        List<Integer> ids = Lists.newArrayList(id, id+1);
        operationLogService.dsTest(id);
        this.testAssertDataContext.put("ids", ids);
        LOGGER.debug(ids.toString());

    }

    @Override
    public void end() throws Exception {
        DataSourceContextHolder.removeCurrentDataSourceKey();
    }
}
