package net.engining.datasource.autoconfigure.autotest.jdbc.support;

import cn.hutool.core.util.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author Eric Lu
 * @create 2019-11-05 18:56
 **/
@Service
public class DbService {
    /** logger */
    private static final Logger log = LoggerFactory.getLogger(DbService.class);

    @Autowired
    private OperAdtLogDao operAdtLogDao;

    public Integer dsTest(int id){
        OperAdtLog operAdtLog = new OperAdtLog(
                id,
          "luxue",
          "/echo/mvc111",
          "{key1:111}",
          LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
          0
        );
        return operAdtLogDao.insert(operAdtLog);
    }

    public OperAdtLog fetch(Integer id) {
        return operAdtLogDao.selectByPrimaryKey(id);
    }

}
