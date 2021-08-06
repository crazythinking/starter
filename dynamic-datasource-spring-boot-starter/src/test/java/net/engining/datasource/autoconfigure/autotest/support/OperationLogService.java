package net.engining.datasource.autoconfigure.autotest.support;

import com.google.common.collect.Lists;
import net.engining.datasource.autoconfigure.autotest.support.OperAdtLogDao;
import net.engining.gm.entity.dto.OperAdtLogDto;
import net.engining.pg.support.utils.DateUtilsExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author Eric Lu
 * @create 2019-11-05 18:56
 **/
@Service
public class OperationLogService {

    @Autowired
    private OperAdtLogDao operAdtLogDao;

    public long dsTest(int id){
        OperAdtLogDto operAdtLog = new OperAdtLogDto();
        operAdtLog.setId(id);
        operAdtLog.setLoginId("luxue");
        operAdtLog.setRequestUri("/echo/mvc111");
        operAdtLog.setRequestBody("{key1:111}");
        operAdtLog.setOperTime(DateUtilsExt.localDateTimeToDate(LocalDateTime.now()));

        OperAdtLogDto operAdtLog2 = new OperAdtLogDto();
        operAdtLog2.setId(id+1);
        operAdtLog2.setLoginId("luxue2");
        operAdtLog2.setRequestUri("/echo/mvc222");
        operAdtLog2.setRequestBody("{key1:222}");
        operAdtLog2.setOperTime(DateUtilsExt.localDateTimeToDate(LocalDateTime.now()));

        return operAdtLogDao.save(Lists.newArrayList(operAdtLog,operAdtLog2));
    }

    public OperAdtLogDto fetch(Integer id) {
        return operAdtLogDao.findByPrimeryKey(id);
    }

}
