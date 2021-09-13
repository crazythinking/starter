package net.engining.datasource.autoconfigure.autotest.jdbc.support;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * 面向单表的, 基于Spring-Data-JDBC Repository操作的存储层
 * @author : Eric Lu
 * @version :
 * @date : 2021-08-24 16:42
 * @since :
 **/
@Repository
public interface PgKeyContextJdbcRepository extends PagingAndSortingRepository<PgKeyContext, PgKeyContextKey> {

}
