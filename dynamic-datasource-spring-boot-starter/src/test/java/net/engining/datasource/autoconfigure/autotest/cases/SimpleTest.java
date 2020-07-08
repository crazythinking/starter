package net.engining.datasource.autoconfigure.autotest.cases;

import net.engining.datasource.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import net.engining.gm.config.autoconfigure.aop.SpecifiedDataSource;
import net.engining.pg.support.core.context.DataSourceContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Eric Lu
 * @date 2019-09-21 23:58
 **/
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
        PgIdTestEnt pgIdTestEnt = em.find(PgIdTestEnt.class, this.testAssertDataContext.get("primerykey4default"));
        Assert.notNull(pgIdTestEnt, "has record by datasource default");

        //Assert.isNull(this.testAssertDataContext.get("primerykey4one"), "has not record by datasource one");
    }

    @Override
    public void testProcess() throws Exception {
        Long id = dbService.dsTest4defautDataSource();
        LOGGER.debug("get primery key for datasource default: {}", id);
        this.testAssertDataContext.put("primerykey4default", id);

        callDbOpt4DataSourceOne();

    }

    @SpecifiedDataSource(value = "one")
    private void callDbOpt4DataSourceOne(){
        try {
            Long id2 = dbService.dsTest();
            LOGGER.debug("get primery key for datasource one: {}", id2);
            this.testAssertDataContext.put("primerykey4one", id2);
        }
        catch (Exception e){

        }
    }

    @Override
    public void end() throws Exception {
        DataSourceContextHolder.removeCurrentDataSourceKey();
    }
}
