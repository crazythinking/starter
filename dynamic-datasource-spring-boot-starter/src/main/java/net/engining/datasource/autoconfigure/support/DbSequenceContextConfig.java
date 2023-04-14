package net.engining.datasource.autoconfigure.support;

import cn.hutool.extra.spring.SpringUtil;
import com.google.common.collect.Table;
import net.engining.pg.support.db.DbType;
import net.engining.pg.support.db.sequence.DB2SequenceOperator;
import net.engining.pg.support.db.sequence.JPASequenceFactory;
import net.engining.pg.support.db.sequence.MysqlSequenceOperator;
import net.engining.pg.support.db.sequence.OracleSequenceOperator;
import net.engining.pg.support.db.sequence.SequenceOperator;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.vendor.Database;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 数据库Sequence相关组件配置
 *
 * @author Eric Lu
 * @date 2019-07-15 12:23
 **/
@EnableConfigurationProperties(value = {
        JpaProperties.class
})
@Configuration
public class DbSequenceContextConfig {

    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(DbSequenceContextConfig.class);

    @Bean
    public SequenceOperator sequenceOperator(JpaProperties jpaProperties) {
        //默认数据源的SequenceOperator
        SequenceOperator sequenceOperator = null;
        if (Database.DB2.equals(jpaProperties.getDatabase())) {
            sequenceOperator = new DB2SequenceOperator();
        } else if (Database.ORACLE.equals(jpaProperties.getDatabase())) {
            sequenceOperator = new OracleSequenceOperator();
        } else if (Database.MYSQL.equals(jpaProperties.getDatabase())||Database.H2.equals(jpaProperties.getDatabase())) {
            sequenceOperator = new MysqlSequenceOperator();
        } else {
            LOGGER.warn("该数据库:{}, 不支持Sequence操作，Bean sequenceOperator 将为空值", jpaProperties.getDatabase());
        }

        //注册其他数据源的SequenceOperator
        registerSequenceOperators(jpaProperties.getDatabase().name().toLowerCase());
        return sequenceOperator;
    }

    /**
     * 批量插入需要创建此工厂类
     *
     * @return 批量插入需要创建此工厂类
     */
    @Bean
    public JPASequenceFactory jpaSequenceFactory() {
        return new JPASequenceFactory();
    }

    private void registerSequenceOperators(String excludedDbType) {
        DefaultListableBeanFactory beanFactory =
                (DefaultListableBeanFactory) SpringUtil.getApplicationContext().getAutowireCapableBeanFactory();

        Table<String, DbType, DataSource> dataSourceTable = SpringUtil.getBean(Utils.MULTIPLE_DATA_SOURCE_TABLE);
        SequenceOperator sequenceOperator = null;
        if (ValidateUtilExt.isNotNullOrEmpty(dataSourceTable)) {
            for (String key : dataSourceTable.rowKeySet()){
                Map<DbType, DataSource> map = dataSourceTable.row(key);
                for (DbType dbType : map.keySet()){
                    //对于SequenceOperator的实例而言，其只有在getNextValue时才会根据当前线程绑定的EntityManager确定当前的数据源
                    //因此只需要针对不同数据类型实例化SequenceOperator
                    if (!dbType.getValue().toLowerCase().equals(excludedDbType)
                            && !beanFactory.containsSingleton(dbType.getValue()+"-sequenceOperator")){
                        if (DbType.DB2.equals(dbType)) {
                            sequenceOperator = new DB2SequenceOperator();
                        } else if (DbType.Oracle.equals(dbType)) {
                            sequenceOperator = new OracleSequenceOperator();
                        } else if (DbType.MySQL.equals(dbType) || DbType.H2.equals(dbType)) {
                            sequenceOperator = new MysqlSequenceOperator();
                        } else {
                            LOGGER.warn("该数据库:{}, 不支持Sequence操作，Bean sequenceOperator 将为空值", dbType.getValue());
                        }
                    }
                    else {
                        sequenceOperator = null;
                    }

                    if (ValidateUtilExt.isNotNullOrEmpty(sequenceOperator)) {
                        beanFactory.registerSingleton(
                                dbType.getValue()+"-sequenceOperator", sequenceOperator
                        );
                    }
                }
            }
        }
    }
}
