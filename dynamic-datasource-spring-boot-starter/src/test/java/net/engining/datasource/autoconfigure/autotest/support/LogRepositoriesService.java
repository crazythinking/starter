package net.engining.datasource.autoconfigure.autotest.support;

import net.engining.gm.entity.dto.OperAdtLogDto;

import java.util.List;

/**
 * Log 相关数据存储服务
 * @author Eric Lu
 * @date 2021-08-06 15:57
 **/
public interface LogRepositoriesService {

    long save(List<OperAdtLogDto> operAdtLogs);

    long save2Ck(List<OperAdtLogDto> operAdtLogList);

    OperAdtLogDto selectByPrimeryKey(Integer id);

    <T> List<T> fetchByLogin(String login);

    <T> List<T> fetchByLogin4Ck(String login);
}
