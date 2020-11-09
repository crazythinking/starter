package net.engining.bustream.autotest.support;

import net.engining.bustream.autotest.AutoConfigureTestApplication;
import net.engining.pg.support.core.context.ApplicationContextHolder;
import net.engining.pg.support.testcase.AbstractJUnit4SpringContextTestsWithServlet;
import net.engining.pg.support.testcase.AbstractJUnit4SpringContextTestsWithoutServlet;
import net.engining.pg.support.testcase.support.TestCommonLogic;
import org.h2.tools.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * 单元测试模版类，可作为抽象类继承，也可作为模版；
 *
 * @author luxue
 *
 */
@ActiveProfiles(profiles={
		"autotest",
})
@SpringBootTest(classes = {
		AutoConfigureTestApplication.class
})
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractTestCaseTemplate extends AbstractJUnit4SpringContextTestsWithoutServlet {

	private static final Logger log = LoggerFactory.getLogger(AbstractTestCaseTemplate.class);

	/**
	 * 由实现该接口的抽象类实现，用于统一的进行整体测试数据初始化，通常结合init项目;
	 * 针对所有测试，只执行一次，且必须为static void
	 *
	 * @throws Exception
	 */
	@BeforeClass
	public static void init4Test() throws Exception {

	}

	/**
	 * 针对所有测试，只执行一次，且必须为static void
	 *
	 * @throws Exception
	 */
	@AfterClass
	public static void tearDown4Test() throws Exception {
		//使用H2的测试需要关闭
		TestCommonLogic.closeH2();
		closeH2();

	}

	private static void closeH2(){
		//使用H2的测试需要关闭
		try {
			Server h2tcp = ApplicationContextHolder.getBean("h2tcpOne");
			h2tcp.stop();
			log.info("H2-one TCP server is closed");
		}
		catch (Exception e){
			log.info("H2-one TCP server not exists");
		}
	}

}
