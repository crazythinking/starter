package net.engining.datasource.autoconfigure.autotest.jdbc.support;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;

import java.util.List;

/**
 * 所有的 DAO 都应继承该类，该类封装了{@link JdbcTemplate} 和{@link NamedParameterJdbcTemplate}，并提供了关于他们的使用方法.
 */
public class BaseDao implements Dao{

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public BaseDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public <R> R queryForObject(String sql, Class<R> requiredType, SqlParameterSource params) {
        return queryForObject(sql, new SingleColumnRowMapper<R>(requiredType), params);
    }

    @Override
    public  <R> R queryForObject(String sql, RowMapper<R> rowMapper, SqlParameterSource params) {
        // namedParameterJdbcTemplate 的 queryForObject 方法在结果不是1时会报错
        List<R> results = namedParameterJdbcTemplate.query(
                sql,
                params,
                rowMapper);
        if(results.isEmpty()){
            return null;
        }
        return results.get(0);
    }

    @Override
    public <R> List<R> query(String sql, RowMapper<R> rowMapper) {
        return namedParameterJdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public <R> List<R> query(String sql, RowMapper<R> rowMapper, SqlParameterSource params) {
        return namedParameterJdbcTemplate.query(sql, params, rowMapper);
    }

    @Override
    public int update(String sql, SqlParameterSource params) {
        return namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public int update(String sql, SqlParameterSource params, KeyHolder keyHolder) {
        return namedParameterJdbcTemplate.update(sql, params, keyHolder);
    }

    @Override
    public int[] batchUpdate(String sql, SqlParameterSource... params) {
        return namedParameterJdbcTemplate.batchUpdate(sql, params);
    }
}