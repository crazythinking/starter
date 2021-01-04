package net.engining.datasource.autoconfigure.autotest.jdbc.support;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;

import java.util.List;

/**
 * @author yuanquan
 *         on 4/13/2017
 */
public interface Dao {

    /**
     * 查询单个 Primitive 对象
     * @param sql
     * @param requiredType
     * @param params
     * @param <R>
     * @return
     */
    <R> R queryForObject(String sql, Class<R> requiredType, SqlParameterSource params);

    /**
     * 查询单个复杂对象
     * @param sql
     * @param rowMapper
     * @param params
     * @param <R>
     * @return
     */
    <R> R queryForObject(String sql, RowMapper<R> rowMapper, SqlParameterSource params);

    /**
     * 查询对象列表
     * @param sql
     * @param rowMapper
     * @param <R>
     * @return
     */
    <R> List<R> query(String sql, RowMapper<R> rowMapper);

    /**
     * 查询对象列表
     * @param sql
     * @param rowMapper
     * @param params
     * @param <R>
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
     * @param sql
     * @param params
     * @return
     */
    int update(String sql, SqlParameterSource params);

    /**
     * 执行 insert 并将生成的主键使用 keyHolder 返回
     * @param sql
     * @param params
     * @param keyHolder
     * @return
     */
    int update(String sql, SqlParameterSource params, KeyHolder keyHolder);

    /**
     * 只编译一次 sql，批量执行 params 传入的参数
     * @param sql
     * @param params
     * @return
     */
    int[] batchUpdate(String sql, SqlParameterSource... params);
}
