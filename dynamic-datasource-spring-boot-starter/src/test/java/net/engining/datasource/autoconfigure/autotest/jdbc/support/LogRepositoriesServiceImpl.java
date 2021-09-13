package net.engining.datasource.autoconfigure.autotest.jdbc.support;

import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Lists;
import net.engining.datasource.autoconfigure.autotest.support.LogRepositoriesService;
import net.engining.datasource.autoconfigure.support.TransactionalEvent;
import net.engining.gm.aop.SpecifiedDataSource;
import net.engining.gm.entity.dto.OperAdtLogDto;
import net.engining.gm.entity.model.jdbc.OperAdtLog;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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

    @Autowired
    PgKeyContextJdbcRepository pgKeyContextJdbcRepository;

    @Transactional(rollbackFor = Exception.class)
    public long save1() {
        PgKeyContext pgKeyContext = new PgKeyContext();
        pgKeyContext.setPgKeyContextKey(new PgKeyContextKey(RandomUtil.randomInt(), RandomUtil.randomInt()));
        pgKeyContext.setSetupDate(new Date());
        pgKeyContext.setJpaVersion(0);
        pgKeyContextJdbcRepository.save(pgKeyContext);
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long save(List<OperAdtLogDto> operAdtLogs) {
        long n = extractedSave(operAdtLogs);
        save1();
        //发布事件
        for (OperAdtLogDto operAdtLogDto : operAdtLogs){
            applicationContext.publishEvent(new TransactionalEvent<>(operAdtLogDto));
        }
        return n;
    }

    private int extractedSave(List<OperAdtLogDto> operAdtLogs) {
        List<OperAdtLog> entitys = Lists.newArrayList();
        operAdtLogs.forEach(operAdtLogDto -> {
            OperAdtLog entity = new OperAdtLog();
            entity.setId(operAdtLogDto.getId());
            entity.setLoginId(operAdtLogDto.getLoginId());
            entity.setRequestUri(operAdtLogDto.getRequestUri());
            entity.setRequestBody(operAdtLogDto.getRequestBody());
            entity.setOperTime(operAdtLogDto.getOperTime());
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
