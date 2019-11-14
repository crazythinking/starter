package net.engining.datasource.autoconfigure.autotest.cases;

import net.engining.datasource.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import net.engining.pg.support.core.context.DataSourceContextHolder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Eric Lu
 * @create 2019-09-21 23:58
 **/
public class SimpleTest extends AbstractTestCaseTemplate {

    @Autowired
    DbService dbService;

    @Override
    public void initTestData() throws Exception {
        //指定当前线程使用的数据库datasource
        DataSourceContextHolder.setCurrentDataSourceKey("one");
    }

    @Override
    public void assertResult() throws Exception {

    }

    @Override
    public void testProcess() throws Exception {
        dbService.dsTest();
    }

    @Override
    public void end() throws Exception {

    }
}
