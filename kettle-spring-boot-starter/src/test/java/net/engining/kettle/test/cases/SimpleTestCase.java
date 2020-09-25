package net.engining.kettle.test.cases;

import net.engining.kettle.service.KettleManagerService;
import net.engining.kettle.test.support.AbstractTestCaseTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * kettle 简单测试
 *
 * @author 陈宝
 * @version 1.0
 * @date 2020/9/25 14:57
 * @since 1.0
 */
public class SimpleTestCase  extends AbstractTestCaseTemplate {

    /**
     * kettle 服务
     */
    @Autowired
    private KettleManagerService kettleManagerService;
    @Override
    public void initTestData() throws Exception {

    }

    @Override
    public void assertResult() throws Exception {

    }

    @Override
    public void testProcess() throws Exception {
        kettleManagerService.defaultExecute();
    }

    @Override
    public void end() throws Exception {

    }
}
