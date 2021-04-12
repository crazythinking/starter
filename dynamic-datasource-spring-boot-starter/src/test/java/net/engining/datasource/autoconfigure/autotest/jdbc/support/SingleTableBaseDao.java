package net.engining.datasource.autoconfigure.autotest.jdbc.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.KeyHolder;

/**
 * 所有的处理单表的 DAO 都应继承该类，该类封装了{@link SimpleJdbcInsert} 利用数据库 Metadata 进行插入操作
 */
public abstract class SingleTableBaseDao<K, E> implements SimpleTableDao<K, E>{

    private JdbcTemplate jdbcTemplate;

    private SimpleJdbcInsert insert;

    protected int insert(SqlParameterSource params){
        return this.insert.execute(params);
    }

    protected Number insertAndReturnKey(SqlParameterSource params){
        return this.insert.executeAndReturnKey(params);
    }

    protected KeyHolder insertAndReturnKeyHolder(SqlParameterSource params){

        return this.insert.executeAndReturnKeyHolder(params);
    }

    protected int[] insertBatch(SqlParameterSource... batch){
        return this.insert.executeBatch(batch);
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

        this.insert = new SimpleJdbcInsert(this.jdbcTemplate.getDataSource())
                .withTableName(getTableName())
                //指定了自动生成key的columns，insert会被排除
                .usingGeneratedKeyColumns(getKeyColumns());
    }

    protected abstract String getTableName();

    /**
     * 用于指定主键对应的Columns；
     * 此处与insert操作相关，被指定的Columns被作为自动生成的，因此会被insert排除掉；
     */
    protected abstract String[] getKeyColumns();
}
