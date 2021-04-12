package net.engining.datasource.autoconfigure.autotest.jdbc.support;

import java.util.List;

/**
 * 单表操作的使用 DAO 接口
 * @param <K> 主键类型
 * @param <E> Entity 类型
 */
public interface SimpleTableDao<K, E> {

    /**
     * 将 entity 插入数据库
     * @param entity
     * @return 影响的 row 的个数
     */
    int insert(E entity);

    /**
     * 将 entity 插入数据库，并返回自动生成的 key
     * @param entity
     * @return
     */
    K insertAndReturnKey(E entity);

    /**
     * 将 entitys 以批量的方式插入数据库
     * @param entitys
     * @return 影响的 row 的个数
     */
    int[] insert(List<E> entitys);

    /**
     * 根据新传入的 entity 更新数据库中的原纪录
     * @param entity
     * @return 影响的 row 的个数
     */
    int updateByPrimaryKey(E entity);

    /**
     * 根据新传入的 entity 更新数据库中的原纪录，只更新 entity 中值不为 null 的字段
     * @param entity
     * @return 影响的 row 的个数，如果 entity 中的字段均为null，则返回0
     */
    int updateByPrimaryKeySelective(E entity);

    /**
     * 根据主键查询 entity
     * @param key
     * @return
     */
    E selectByPrimaryKey(K key);

    /**
     * 根据主键删除 entity
     * @param key
     * @return 影响的 row 的个数
     */
    int delete(K key);

    /**
     * 根据主键以批量的方式删除 entitys
     * @param keys
     * @return
     */
    int[] delete(List<K> keys);
}
