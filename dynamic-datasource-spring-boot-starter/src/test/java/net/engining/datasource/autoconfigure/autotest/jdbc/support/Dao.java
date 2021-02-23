package net.engining.datasource.autoconfigure.autotest.jdbc.support;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;

import java.util.List;

public interface Dao {

    /**
     * 查询单个 Primitive 对象
     */
    <R> R queryForObject(String sql, Class<R> requiredType, SqlParameterSource params);

    /**
     * 查询单个复杂对象
     */
    <R> R queryForObject(String sql, RowMapper<R> rowMapper, SqlParameterSource params);

    /**
     * 查询对象列表
     */
    <R> List<R> query(String sql, RowMapper<R> rowMapper);

    /**
     * 查询对象列表
     * @return
     */
    <R> List<R> query(String sql, RowMapper<R> rowMapper, SqlParameterSource params);

//    /**
//     * 分页查询
//     * @param sql
//     * @param rowMapper
//     * @param params
//     * @param <R>
//     * @return
//     */
//    <R> Page<R> queryAndPaging(String sql, Pageable pageRequest, RowMapper<R> rowMapper, SqlParameterSource params);

    /**
     * 执行任意 insert、update、delete
     */
    int update(String sql, SqlParameterSource params);

    /**
     * 执行 insert 并将生成的主键使用 keyHolder 返回
     */
    int update(String sql, SqlParameterSource params, KeyHolder keyHolder);

    /**
     * 只编译一次 sql，批量执行 params 传入的参数
     */
    int[] batchUpdate(String sql, SqlParameterSource... params);
}
