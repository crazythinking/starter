package net.engining.datasource.autoconfigure.autotest.jpa.cases;

import net.engining.datasource.autoconfigure.autotest.jpa.support.AbstractTestCaseTemplate;
import net.engining.datasource.autoconfigure.autotest.jpa.support.DbService;
import net.engining.datasource.autoconfigure.autotest.jpa.support.PgIdTestEnt1;
import net.engining.pg.support.core.context.DataSourceContextHolder;
import org.assertj.core.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Eric Lu
 * @date 2019-09-21 23:58
 **/
@ActiveProfiles(profiles={
        "autotest.hikari",
        "db.common",
//		"hikari.h2",
//		"druid.mysql",
		"hikari.mysql",
})
public class SimpleTest extends AbstractTestCaseTemplate {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTest.class);

    @PersistenceContext
    private EntityManager em;

    @Autowired
    DbService dbService;

    @Override
    public void initTestData() throws Exception {
        //指定当前线程使用的数据库datasource
        //DataSourceContextHolder.setCurrentDataSourceKey("one");
    }

    @Override
    public void assertResult() throws Exception {
        PgIdTestEnt1 pgIdTestEnt1 = em.find(PgIdTestEnt1.class, this.testAssertDataContext.get("primerykey4default"));
        Assert.notNull(pgIdTestEnt1, "has record by datasource default");

        Assert.isNull(this.testAssertDataContext.get("primerykey4one"), "has not record by datasource one");
    }

    @Override
    public void testProcess() throws Exception {
        Long id = dbService.dsTest4defautDataSource();
        LOGGER.debug("get primery key for datasource default: {}", id);
        this.testAssertDataContext.put("primerykey4default", id);

        //切换到数据库one，不存在该表抛异常
        Assertions.assertThatThrownBy(() -> callDbOpt4DataSourceOne4ThrowException())
                .isInstanceOf(InvalidDataAccessResourceUsageException.class);

    }

    public void callDbOpt4DataSourceOne4ThrowException() throws Exception{
            Long id2 = dbService.dsTest();
            LOGGER.debug("get primery key for datasource one: {}", id2);
            this.testAssertDataContext.put("primerykey4one", id2);
    }

    @Override
    public void end() throws Exception {
        DataSourceContextHolder.removeCurrentDataSourceKey();
    }
}
