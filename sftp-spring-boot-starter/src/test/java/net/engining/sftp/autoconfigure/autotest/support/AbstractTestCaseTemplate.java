package net.engining.sftp.autoconfigure.autotest.support;

import net.engining.pg.support.testcase.AbstractJUnit4SpringContextTestsWithoutServlet;
import net.engining.sftp.autoconfigure.autotest.AutoConfigureTestApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
		"autotest"
})
@SpringBootTest(classes = {
		AutoConfigureTestApplication.class
})
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractTestCaseTemplate extends AbstractJUnit4SpringContextTestsWithoutServlet {

	/**
	 * 由实现该接口的抽象类实现，用于统一的进行整体测试数据初始化，通常结合init项目;
	 * 针对所有测试，只执行一次，且必须为static void
	 *
	 * @throws Exception
	 */
	@BeforeAll
	public static void init4Test() throws Exception {

	}

	/**
	 * 针对所有测试，只执行一次，且必须为static void
	 *
	 * @throws Exception
	 */
	@AfterAll
	public static void tearDown4Test() throws Exception {

	}

}
