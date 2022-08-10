package net.engining.datasource.autoconfigure.autotest.jdbc.support;

import net.engining.gm.entity.model.jdbc.OperAdtLog;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 面向单表的, 基于Spring-Data-JDBC Repository操作的存储层
 * @author : Eric Lu
 * @version :
 * @date : 2021-08-24 16:42
 * @since :
 **/
@Repository
public interface OperAdtLogJdbcRepository extends PagingAndSortingRepository<OperAdtLog, Integer> {

    /**
     * 注意这里返回的并不是原始的OperAdtLog，而是额外定义的DTO，需要有对应的RowMapper
     */
    @Query(
            value = "select * from OPER_ADT_LOG o where o.LOGIN_ID = :loginId",
            rowMapperClass = OperAdtLogExtDto.OperAdtLogExtDtoRowMapper.class
    )
    List<OperAdtLogExtDto> findByLoginId(@Param("loginId") String loginId);

    /**
     * 基于Named-SQL配置文件，默认在classpath下”META-INF/jdbc-named-queries.properties“
     */
    @Query(
            name = "OperAdtLog.fetchWhereUri",
            rowMapperClass = OperAdtLogExtDto.OperAdtLogExtDtoRowMapper.class
    )
    List<OperAdtLogExtDto> fetchWhereUri(@Param("uri") String uri);

}
