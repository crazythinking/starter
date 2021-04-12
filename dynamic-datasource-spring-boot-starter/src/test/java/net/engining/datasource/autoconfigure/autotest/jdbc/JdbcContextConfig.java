package net.engining.datasource.autoconfigure.autotest.jdbc;

import net.engining.datasource.autoconfigure.autotest.jdbc.support.BaseDao;
import net.engining.datasource.autoconfigure.autotest.jdbc.support.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * only for jdbc without other orms
 *
 * @author : Eric Lu
 * @version :
 * @date : 2020-12-30 9:51
 * @since :
 **/
@Configuration
@EnableJdbcRepositories({
        "net.engining.datasource.autoconfigure.autotest.jdbc.support"
})
@EnableTransactionManagement(mode= AdviceMode.ASPECTJ)
public class JdbcContextConfig {

    /**
     * 数据库连接
     */
    @Autowired
    DataSource dataSource;

    /**
     * 保留纯jdbc方式操作数据库的能力
     * @return JdbcTemplate
     */
    @Bean
    public JdbcTemplate jdbcTemplate(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
        return jdbcTemplate;

    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(JdbcOperations jdbcTemplate) {
        return new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Bean
    public Dao daoDefault(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new BaseDao(namedParameterJdbcTemplate);
    }
}
