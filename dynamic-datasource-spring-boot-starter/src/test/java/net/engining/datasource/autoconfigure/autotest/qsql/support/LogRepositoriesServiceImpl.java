package net.engining.datasource.autoconfigure.autotest.qsql.support;

import com.google.common.collect.Lists;
import com.querydsl.core.Tuple;
import com.querydsl.sql.SQLBindings;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.SQLInsertClause;
import net.engining.datasource.autoconfigure.autotest.jpa.support.OperAdtLogJpaRepository;
import net.engining.datasource.autoconfigure.autotest.support.LogRepositoriesService;
import net.engining.datasource.autoconfigure.autotest.support.OperAdtLogProjection;
import net.engining.datasource.autoconfigure.support.TransactionalEvent;
import net.engining.datasource.autoconfigure.support.Utils;
import net.engining.gm.aop.SpecifiedDataSource;
import net.engining.gm.entity.dto.OperAdtLogDto;
import net.engining.gm.entity.model.qsql.QSqlOperAdtLog;
import net.engining.pg.support.core.context.DataSourceContextHolder;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
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
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Component
public class LogRepositoriesServiceImpl implements LogRepositoriesService {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(LogRepositoriesServiceImpl.class);

    @Autowired
    @Qualifier(Utils.SQL_QUERY_FACTORY_MAP)
    Map<String, SQLQueryFactory> sqlQueryFactoryMap;

    @Autowired
    OperAdtLogJpaRepository operAdtLogJpaRepository;

    @Autowired
    ApplicationContext applicationContext;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long save(List<OperAdtLogDto> operAdtLogList){
        long n = extractedSave(operAdtLogList);
        //发布事件
        for (OperAdtLogDto operAdtLogDto : operAdtLogList){
            applicationContext.publishEvent(new TransactionalEvent<>(operAdtLogDto));
        }
        return n;

    }

    private long extractedSave(List<OperAdtLogDto> operAdtLogList) {
        String key = DataSourceContextHolder.getCurrentDataSourceKey();
        SQLQueryFactory sqlQueryFactory = sqlQueryFactoryMap.get(key);

        QSqlOperAdtLog qSqlOperAdtLog = QSqlOperAdtLog.operAdtLog;
        SQLInsertClause insertClause = sqlQueryFactory.insert(qSqlOperAdtLog);
        //设置insert为一条批量语句，而非多条insert语句，可减少对数据库的IO操作，提高性能
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

        //将参数值带入sql语句
        insertClause.setUseLiterals(true);
        for (SQLBindings sqlBindings : insertClause.getSQL()){
            LOGGER.debug(sqlBindings.getSQL());
        }

        return insertClause.execute();
    }

    @Override
    @SpecifiedDataSource("one")
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public long save2Ck(List<OperAdtLogDto> operAdtLogList) {
        return extractedSave(operAdtLogList);
    }

    @Override
    @Transactional(readOnly = true)
    public OperAdtLogDto selectByPrimeryKey(Integer id){
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

    @Override
    public <T> List<T> fetchByLogin4Ten(String login) {
        return null;
    }

    @Override
    public List<OperAdtLogProjection> fetchByLogin(String login){
        return operAdtLogJpaRepository.findByLoginId(
                login,
                OperAdtLogProjection.class
        );
    }

    @Override
    @SpecifiedDataSource("one")
    @Transactional(readOnly = true)
    public List<OperAdtLogDto> fetchByLogin4Ck(String login) {
        String key = DataSourceContextHolder.getCurrentDataSourceKey();
        SQLQueryFactory sqlQueryFactory = sqlQueryFactoryMap.get(key);

        QSqlOperAdtLog qSqlOperAdtLog = QSqlOperAdtLog.operAdtLog;
        List<Tuple> tuples = sqlQueryFactory
                .select(qSqlOperAdtLog.id, qSqlOperAdtLog.loginId, qSqlOperAdtLog.operTime, qSqlOperAdtLog.requestUri)
                .from(qSqlOperAdtLog)
                .where(qSqlOperAdtLog.loginId.eq(login))
                .fetch();

        List<OperAdtLogDto> operAdtLogDtos = Lists.newArrayList();
        tuples.forEach(tuple -> {
            OperAdtLogDto operAdtLogDto = new OperAdtLogDto();
            operAdtLogDto.setId(tuple.get(qSqlOperAdtLog.id));
            operAdtLogDto.setLoginId(tuple.get(qSqlOperAdtLog.loginId));
            operAdtLogDto.setOperTime(tuple.get(qSqlOperAdtLog.operTime));
            operAdtLogDto.setRequestUri(tuple.get(qSqlOperAdtLog.requestUri));
            operAdtLogDtos.add(operAdtLogDto);
        });

        return operAdtLogDtos;
    }

    private static Timestamp getDate(Date date){
        if(ValidateUtilExt.isNotNullOrEmpty(date)){
            return new Timestamp(date.getTime());
        }else{
            return null;
        }
    }

}
