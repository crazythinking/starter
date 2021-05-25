package net.engining.zeebe.spring.client.ext.autotest.cases;

import net.engining.zeebe.spring.client.ext.ZeebeSimpleAdminHandler;
import net.engining.zeebe.spring.client.ext.autotest.support.AbstractTestCaseTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author : Eric Lu
 * @date : 2020-10-31 18:25
 **/
@ActiveProfiles(profiles = {
        "zeebe",
        "zeebe.dev"
})
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
public class DemoProcessTestCase extends AbstractTestCaseTemplate {

    @Autowired
    DemoProcessStarterService starterService;

    @Autowired
    ZeebeSimpleAdminHandler zeebeSimpleAdminHandler;

    @Override
    public void initTestData() throws Exception {

    }

    @Override
    public void assertResult() throws Exception {

    }

    @Override
    public void testProcess() throws Exception {
        starterService.startProcess();

        //等待Worker线程获取到消息并处理
        Thread.sleep(20000);

        //拓扑
        zeebeSimpleAdminHandler.getTopology();

        //部署
        zeebeSimpleAdminHandler.deploy(
                "D:/idea-workspace/power-gears/starter/zeebe-ext-spring-boot-starter/src/test/resources/test-process1.bpmn"
        );
    }


    @Override
    public void end() throws Exception {

    }
}
