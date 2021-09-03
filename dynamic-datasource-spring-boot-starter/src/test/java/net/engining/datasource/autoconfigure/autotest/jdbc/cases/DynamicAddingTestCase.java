package net.engining.datasource.autoconfigure.autotest.jdbc.cases;

import com.zaxxer.hikari.HikariConfig;
import net.engining.datasource.autoconfigure.autotest.jdbc.support.AbstractTestCaseTemplate;
import net.engining.datasource.autoconfigure.autotest.support.OperationLogBizService;
import net.engining.datasource.autoconfigure.support.DynamicDataSourceEndpoint;
import net.engining.pg.db.props.DynamicHikariDataSourceProperties;
import net.engining.pg.support.core.context.ApplicationContextHolder;
import org.assertj.core.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

/**
 * @author Eric Lu
 * @date 2019-09-21 23:58
 **/
@ActiveProfiles(profiles={
        "autotest.hikari",
        "db.common",
		"hikari.clickhouse",
        //"hikari.h2"
})
public class DynamicAddingTestCase extends AbstractTestCaseTemplate {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicAddingTestCase.class);

    @Autowired
    DynamicDataSourceEndpoint endpoint;

    @Autowired
    DynamicHikariDataSourceProperties properties;

    @Autowired
    OperationLogBizService operationLogService;

    @Override
    public void initTestData() throws Exception {

    }

    @Override
    public void assertResult() throws Exception {
        DataSource dataSource = ApplicationContextHolder.getBean(DataSource.class);
        LOGGER.debug(dataSource.toString());

    }

    public void callDataSourceTen4ThrowException() throws Exception{
        operationLogService.fetch4Ten("luxue");
    }

    @Override
    public void testProcess() throws Exception {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("org.h2.Driver");
        hikariConfig.setJdbcUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE");
        hikariConfig.setUsername("sa");
        hikariConfig.setPassword("");

        properties.getCfMap().put("ten", hikariConfig);

        endpoint.addDataSource("ten");

        //切换到数据库ten，不存在该表抛异常
        Assertions.assertThatThrownBy(() -> callDataSourceTen4ThrowException())
                .isInstanceOf(InvalidDataAccessResourceUsageException.class);

    }


    @Override
    public void end() throws Exception {

    }
}
