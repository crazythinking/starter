package net.engining.datasource.autoconfigure.autotest.qsql.support;

import com.querydsl.core.Tuple;
import com.querydsl.sql.SQLBindings;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.SQLInsertClause;
import net.engining.datasource.autoconfigure.autotest.support.OperAdtLogDao;
import net.engining.gm.aop.SpecifiedDataSource;
import net.engining.gm.entity.dto.OperAdtLogDto;
import net.engining.gm.entity.model.qsql.QSqlOperAdtLog;
import net.engining.pg.support.core.context.DataSourceContextHolder;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Eric Lu
 * @date 2020-12-30 14:54
 **/
@Repository
public class OperAdtLogDaoImpl implements OperAdtLogDao {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(OperAdtLogDaoImpl.class);

    @Autowired
    Map<String, SQLQueryFactory> sqlQueryFactoryMap;

    @SpecifiedDataSource("one")
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public long save(List<OperAdtLogDto> operAdtLogList){
        String key = DataSourceContextHolder.getCurrentDataSourceKey();
        SQLQueryFactory sqlQueryFactory = sqlQueryFactoryMap.get(key);

        QSqlOperAdtLog qSqlOperAdtLog = QSqlOperAdtLog.operAdtLog;
        SQLInsertClause insertClause = sqlQueryFactory.insert(qSqlOperAdtLog);
        insertClause.setBatchToBulk(true);

        for (OperAdtLogDto operAdtLog : operAdtLogList){
            insertClause
                    .set(qSqlOperAdtLog.id, operAdtLog.getId())
                    .set(qSqlOperAdtLog.operTime, getDate(operAdtLog.getOperTime()))
                    .set(qSqlOperAdtLog.loginId, operAdtLog.getLoginId())
                    .set(qSqlOperAdtLog.jpaVersion, 0)
                    .set(qSqlOperAdtLog.requestBody, operAdtLog.getRequestBody())
                    .set(qSqlOperAdtLog.requestUri, operAdtLog.getRequestUri())
                    ;
            insertClause.addBatch();
        }

        insertClause.setUseLiterals(true);
        for (SQLBindings sqlBindings : insertClause.getSQL()){
            LOGGER.debug(sqlBindings.getSQL());
        }

        return insertClause.execute();
    }

    @SpecifiedDataSource("one")
    @Transactional(readOnly = true)
    public OperAdtLogDto findByPrimeryKey(Integer id){
        String key = DataSourceContextHolder.getCurrentDataSourceKey();
        SQLQueryFactory sqlQueryFactory = sqlQueryFactoryMap.get(key);

        QSqlOperAdtLog qSqlOperAdtLog = QSqlOperAdtLog.operAdtLog;
        Tuple tuple = sqlQueryFactory
                .select(qSqlOperAdtLog.id, qSqlOperAdtLog.loginId, qSqlOperAdtLog.operTime, qSqlOperAdtLog.requestUri)
                .from(qSqlOperAdtLog)
                .where(qSqlOperAdtLog.id.eq(id))
                .fetchOne();

        OperAdtLogDto operAdtLogDto = new OperAdtLogDto();
        operAdtLogDto.setId(tuple.get(qSqlOperAdtLog.id));

        return operAdtLogDto;
    }

    private static Timestamp getDate(Date date){
        if(ValidateUtilExt.isNotNullOrEmpty(date)){
            return new Timestamp(date.getTime());
        }else{
            return null;
        }
    }
}
