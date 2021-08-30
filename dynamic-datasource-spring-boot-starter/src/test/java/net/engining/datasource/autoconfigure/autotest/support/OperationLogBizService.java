package net.engining.datasource.autoconfigure.autotest.support;

import com.google.common.collect.Lists;
import net.engining.gm.entity.dto.OperAdtLogDto;
import net.engining.pg.support.utils.DateUtilsExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Eric Lu
 **/
@Service
public class OperationLogBizService {

    @Autowired
    private LogRepositoriesService logRepositoriesService;

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

        logRepositoriesService.save(Lists.newArrayList(operAdtLog,operAdtLog2));
        logRepositoriesService.save2Ck(Lists.newArrayList(operAdtLog,operAdtLog2));
        return 0;
    }

    public OperAdtLogDto fetch(Integer id) {
        return logRepositoriesService.selectByPrimeryKey(id);
    }

    public <T> List<T> fetch(String loginId) {
        return logRepositoriesService.fetchByLogin(loginId);
    }

    public <T> List<T> fetch4Ck(String loginId) {
        return logRepositoriesService.fetchByLogin4Ck(loginId);
    }

}