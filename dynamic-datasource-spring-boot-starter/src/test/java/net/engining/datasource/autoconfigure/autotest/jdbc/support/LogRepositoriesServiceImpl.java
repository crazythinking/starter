package net.engining.datasource.autoconfigure.autotest.jdbc.support;

import com.google.common.collect.Lists;
import net.engining.datasource.autoconfigure.autotest.support.LogRepositoriesService;
import net.engining.datasource.autoconfigure.support.TransactionalEvent;
import net.engining.gm.aop.SpecifiedDataSource;
import net.engining.gm.entity.dto.OperAdtLogDto;
import net.engining.pg.support.core.context.DataSourceContextHolder;
import net.engining.pg.support.utils.DateUtilsExt;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 面向领域的存储服务，注意这里不应该只是单表的存储层，而是面向Log这个邻域的；
 * 此处可以组合JPA，QueryDSL，JDBC等多项能力；
 *
 * @author Eric Lu
 * @date 2020-12-30 14:54
 **/
@Component
public class LogRepositoriesServiceImpl implements LogRepositoriesService {

    @Autowired
    OperAdtLogJdbcRepository operAdtLogJdbcRepository;

    @Autowired
    OperAdtLogJdbcDao operAdtLogJdbcDao;

    @Autowired
    ApplicationContext applicationContext;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long save(List<OperAdtLogDto> operAdtLogs) {
        long n = extractedSave(operAdtLogs);
        //发布事件
        for (OperAdtLogDto operAdtLogDto : operAdtLogs){
            applicationContext.publishEvent(new TransactionalEvent<>(operAdtLogDto));
        }
        return n;
    }

    private int extractedSave(List<OperAdtLogDto> operAdtLogs) {
        List<OperAdtLog> entitys = Lists.newArrayList();
        operAdtLogs.forEach(operAdtLogDto -> {
            OperAdtLog entity = new OperAdtLog(
                    operAdtLogDto.getId(),
                    operAdtLogDto.getLoginId(),
                    operAdtLogDto.getRequestUri(),
                    operAdtLogDto.getRequestBody(),
                    DateUtilsExt.dateToLocalDateTime(operAdtLogDto.getOperTime()),
                    0
            );
            entitys.add(entity);
        });
        operAdtLogJdbcDao.insertBatch(entitys);
        return 0;
    }

    @Override
    @SpecifiedDataSource("one")
    public long save2Ck(List<OperAdtLogDto> operAdtLogs) {
        return extractedSave(operAdtLogs);
    }

    @Override
    public OperAdtLogDto selectByPrimeryKey(Integer id) {
        Optional<OperAdtLog> jdbcOperAdtLog = operAdtLogJdbcRepository.findById(id);

        OperAdtLogDto operAdtLogDto = new OperAdtLogDto();
        jdbcOperAdtLog.ifPresent(operAdtLog -> BeanUtils.copyProperties(operAdtLog, operAdtLogDto));
        return operAdtLogDto;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<OperAdtLogExtDto> fetchByLogin4Ten(String login) {
        //DataSourceContextHolder.setCurrentDataSourceKey("ten");
        List<OperAdtLogExtDto> operAdtLogExtDtos = operAdtLogJdbcRepository.findByLoginId(login);
        //DataSourceContextHolder.removeCurrentDataSourceKey();
        return operAdtLogExtDtos;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<OperAdtLogExtDto> fetchByLogin(String login) {
        return operAdtLogJdbcRepository.findByLoginId(login);
    }

    @SuppressWarnings("unchecked")
    @Override
    @SpecifiedDataSource("one")
    public List<OperAdtLogExtDto> fetchByLogin4Ck(String login) {
        return operAdtLogJdbcRepository.findByLoginId(login);
    }
}
