package net.engining.datasource.autoconfigure.autotest.jdbc.support;

import com.google.common.collect.Table;
import net.engining.pg.support.core.context.DataSourceContextHolder;
import net.engining.pg.support.db.DbType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 针对单表的 JDBC DAO 都应继承该类; 该类可获得：<br>
 * <li> {@link SimpleJdbcInsert} 利用数据库 Metadata 进行插入操作
 * <li> {@link NamedParameterJdbcTemplate } 具有基本 JDBC 操作集的模板类，允许使用命名参数而不是传统的“?” 占位符
 * <li> {@link JdbcTemplate }
 */
public abstract class AbstractSingleTableJdbcDao {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private SimpleJdbcInsert insert;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("multipleDataSourceTable")
    Table<String, DbType, DataSource> multipleDataSourceTable;

    @Autowired
    public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = namedParameterJdbcTemplate.getJdbcTemplate();
        this.insert = new SimpleJdbcInsert(Objects.requireNonNull(this.jdbcTemplate.getDataSource()))
                .withTableName(getTableName())
        ;
    }

    /**
     * 根据Bean ”multipleDataSourceTable“ 判断当前操作对应的数据库是否支持自增长的主键
     * @return  true=支持自增长
     */
    public boolean isSupportGeneratedKey(){
        boolean isSupport = false;
        String key = DataSourceContextHolder.getCurrentDataSourceKey();
        for (DbType dbType : multipleDataSourceTable.row(key).keySet()){
            switch (dbType) {
                case MySQL:
                    isSupport = true;
                    break;
                case DB2:
                case Oracle:
                case H2:
                case PostgreSQL:
                case ClickHouse:
                    break;
            }
        }
        return isSupport;
    }

    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return namedParameterJdbcTemplate;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public SimpleJdbcInsert getInsert() {
        return insert;
    }

    protected abstract String getTableName();

    /**
     * 用于指定主键对应的Columns；
     * 此处与insert操作相关，被指定的Columns被作为自动生成的，因此会被insert排除掉；
     */
    protected abstract String[] getKeyColumns();
}
