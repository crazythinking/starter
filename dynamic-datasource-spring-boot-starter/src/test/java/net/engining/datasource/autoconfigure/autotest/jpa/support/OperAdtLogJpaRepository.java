package net.engining.datasource.autoconfigure.autotest.jpa.support;

import net.engining.gm.entity.repositroy.ROperAdtLog;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 组合Repository，可继承多个Repository，且不要做任何实现，交由Spring AOP处理；R** 生成的Repo也不要做实现；
 * CustomrizedRepository可用于自行实现数据库存储逻辑；
 *
 * @author Eric Lu
 * @date 2021-08-23 16:20
 **/
@Repository
public interface OperAdtLogJpaRepository extends ROperAdtLog {

    /**
     * 动态DTO projection
     */
    <T> List<T> findByLoginId(String loginId, Class<T> type);

}
