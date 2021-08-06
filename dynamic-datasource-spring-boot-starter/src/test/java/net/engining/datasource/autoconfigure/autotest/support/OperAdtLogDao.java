package net.engining.datasource.autoconfigure.autotest.support;

import net.engining.gm.entity.dto.OperAdtLogDto;
import net.engining.gm.entity.model.OperAdtLog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eric Lu
 * @date 2021-08-06 15:57
 **/
public interface OperAdtLogDao {
    long save(List<OperAdtLogDto> operAdtLogs);

    OperAdtLogDto findByPrimeryKey(Integer id);
}
