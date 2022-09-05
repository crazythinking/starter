package net.engining.metrics.autoconfigure.autotest.cases;

import com.alibaba.management.WispCounterMXBean;
import com.alibaba.wisp.engine.WispCounter;
import com.sun.management.ThreadMXBean;
import net.engining.metrics.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.management.JMX;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

@ActiveProfiles(profiles={
        "metrics.common"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class MxBeanTest extends AbstractTestCaseTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(MxBeanTest.class);

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Override
    public void initTestData() throws Exception {
        // 构造MockMvc
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

    }

    @Override
    public void assertResult() throws Exception {

    }

    @Override
    public void testProcess() throws Exception {
        final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        LOGGER.warn("MBean count:{}", mBeanServer.getMBeanCount());
        ObjectName objectName = new ObjectName("java.lang:type=Threading");
        MBeanInfo mBeanInfo = mBeanServer.getMBeanInfo(objectName);
        LOGGER.warn("'{}' mBeanInfo: {}", objectName.getDomain(), mBeanInfo.toString());
        ObjectInstance objectInstance = mBeanServer.getObjectInstance(objectName);
        LOGGER.warn("{}", objectInstance);
        ThreadMXBean mxBean = JMX.newMBeanProxy(mBeanServer, objectName, ThreadMXBean.class);
        long[] ids= mxBean.getAllThreadIds();
        LOGGER.warn("{}", ids);


    }


    @Override
    public void end() throws Exception {

    }

}
