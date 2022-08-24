package net.engining.datasource.autoconfigure.autotest.jdbc.cases;

import cn.hutool.core.util.RandomUtil;
import com.google.common.base.Joiner;
import net.engining.datasource.autoconfigure.autotest.jdbc.support.AbstractTestCaseTemplate;
import net.engining.datasource.autoconfigure.autotest.jdbc.support.OperAdtLogExtDto;
import net.engining.datasource.autoconfigure.autotest.support.OperationLogBizService;
import net.engining.gm.entity.dto.OperAdtLogDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.Assert;

/**
 * @author Eric Lu
 * @date 2019-09-21 23:58
 **/
@ActiveProfiles(profiles={
        "dynamic.hikari.enable",
        "db.common",
		//"hikari.clickhouse",
        //""hikari.mysql",
        "hikari.h2"
})
public class SimpleTestCase extends AbstractTestCaseTemplate {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTestCase.class);

    @Autowired
    OperationLogBizService operationLogService;

    @Autowired
    private Environment environment;

    @Override
    public void initTestData() {

    }

    @Override
    public void assertResult() {
        OperAdtLogDto operAdtLog = operationLogService.fetch((Integer) this.testAssertDataContext.get("insertKey4default"));
        Assert.notNull(operAdtLog, "has record by default datasource");
    }

    @Override
    public void testProcess() {
        operationLogService.asyncTest();

        int id = RandomUtil.randomInt();
        operationLogService.dsTest(id);
        this.testAssertDataContext.put("insertKey4default", id);

        operationLogService.fetch("luxue").forEach(o -> {
            OperAdtLogExtDto operAdtLogExtDto = (OperAdtLogExtDto) o;
            LOGGER.debug(Joiner.on(";")
                    .join(
                            operAdtLogExtDto.getOperTime(),
                            operAdtLogExtDto.getId(),
                            operAdtLogExtDto.getLoginId(),
                            operAdtLogExtDto.getRequestUri(),
                            operAdtLogExtDto.getHashedRequestBody()
                    )
            );
        });

        if (environment.acceptsProfiles(Profiles.of("hikari.clickhouse"))) {
            operationLogService.fetch4Ck("luxue").forEach(o -> {
                OperAdtLogExtDto operAdtLogExtDto = (OperAdtLogExtDto) o;
                LOGGER.debug(Joiner.on(";")
                        .join(
                                operAdtLogExtDto.getOperTime(),
                                operAdtLogExtDto.getId(),
                                operAdtLogExtDto.getLoginId(),
                                operAdtLogExtDto.getRequestUri(),
                                operAdtLogExtDto.getHashedRequestBody()
                        )
                );
            });
        }

    }


    @Override
    public void end() {

    }
}
