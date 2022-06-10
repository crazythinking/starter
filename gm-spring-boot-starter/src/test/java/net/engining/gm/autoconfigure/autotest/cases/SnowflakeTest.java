package net.engining.gm.autoconfigure.autotest.cases;


import cn.hutool.core.lang.Console;
import net.engining.gm.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import net.engining.gm.autoconfigure.autotest.support.Inter;
import net.engining.pg.support.core.context.ApplicationContextHolder;
import net.engining.pg.support.db.id.generator.SnowflakeSequenceID;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * @author Eric Lu
 **/
@ActiveProfiles(profiles = {
        "snowflake",
})
public class SnowflakeTest extends AbstractTestCaseTemplate {

    @Autowired
    private ConfigurableListableBeanFactory beanFactory;

    @Autowired
    private SnowflakeSequenceID snowflakeSequenceID;

    @Override
    public void initTestData() {

    }

    @Override
    public void assertResult() {

    }

    @Override
    public void testProcess() {
        Map<String, Inter> beansOfType = beanFactory.getBeansOfType(Inter.class);
        Console.log(beansOfType);

        Console.log(snowflakeSequenceID.nextIdLong());
        Console.log(snowflakeSequenceID.toString());

    }

    @Override
    public void end() {

    }

}
