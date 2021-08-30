package net.engining.datasource.autoconfigure.autotest.qsql.cases;

import cn.hutool.core.util.RandomUtil;
import com.google.common.base.Joiner;
import net.engining.datasource.autoconfigure.autotest.qsql.support.AbstractTestCaseTemplate;
import net.engining.datasource.autoconfigure.autotest.support.OperAdtLogProjection;
import net.engining.datasource.autoconfigure.autotest.support.OperationLogBizService;
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
		//"hikari.h2",
        "hikari.clickhouse"
})
public class SimpleTestCase extends AbstractTestCaseTemplate {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTestCase.class);

    @Autowired
    OperationLogBizService operationLogBizService;

    @Override
    public void initTestData() throws Exception {

    }

    @Override
    public void assertResult() throws Exception {
        List<Integer> ids = (List<Integer>) this.testAssertDataContext.get("ids");
        OperAdtLogDto operAdtLogDto = operationLogBizService.fetch(ids.get(0));
        LOGGER.debug(operAdtLogDto.toString());
    }

    @Override
    public void testProcess() throws Exception {
        Integer id = RandomUtil.randomInt();
        List<Integer> ids = Lists.newArrayList(id, id+1);
        operationLogBizService.dsTest(id);
        this.testAssertDataContext.put("ids", ids);
        LOGGER.debug(ids.toString());
        operationLogBizService.fetch("luxue").forEach(o -> {
            OperAdtLogProjection operAdtLogProjection = (OperAdtLogProjection) o;
            LOGGER.debug(Joiner.on(";")
                    .join(
                            operAdtLogProjection.getOperTime(),
                            operAdtLogProjection.getId(),
                            operAdtLogProjection.getLoginId(),
                            operAdtLogProjection.getRequestUri()
                    )
            );
        });

        operationLogBizService.fetch4Ck("luxue").forEach(o -> {
            OperAdtLogProjection operAdtLogProjection = (OperAdtLogProjection) o;
            LOGGER.debug(Joiner.on(";")
                    .join(
                            operAdtLogProjection.getOperTime(),
                            operAdtLogProjection.getId(),
                            operAdtLogProjection.getLoginId(),
                            operAdtLogProjection.getRequestUri()
                    )
            );
        });

    }

    @Override
    public void end() throws Exception {
        DataSourceContextHolder.removeCurrentDataSourceKey();
    }
}
