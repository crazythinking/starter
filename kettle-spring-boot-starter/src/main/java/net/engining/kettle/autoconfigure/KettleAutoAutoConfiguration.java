package net.engining.kettle.autoconfigure;

import net.engining.kettle.common.KettleContextInfo;
import net.engining.kettle.common.KettleTypeEnum;
import net.engining.kettle.prop.KettleCommonProperties;
import net.engining.kettle.prop.KettleContextProperties;
import net.engining.kettle.service.KettleManagerService;
import net.engining.pg.support.core.exception.ErrorCode;
import net.engining.pg.support.core.exception.ErrorMessageException;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Kettle 配置文件
 *
 * @author 陈宝
 * @version 1.0
 * @date 2020/9/23 14:54
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties({
        KettleCommonProperties.class,
        KettleContextProperties.class
})
@ConditionalOnProperty(prefix = "pg.kettle", name = "enabled", matchIfMissing = true)
public class KettleAutoAutoConfiguration {
    /**
     * kettle 配置参数
     */
    @Autowired
    private KettleContextProperties kettleContextProperties;

    /**
     * kettle管理服务
     *
     * @return 管理服务
     */
    @Bean
    public KettleManagerService kettleManagerService() {
        checkProperties();
        return new KettleManagerService();
    }

    /**
     * 错误信息头
     */
    private final static String MSG_HEAD="执行配置中[";
    /**
     * 错误信息尾
     */
    private final static String MSG_TAIL=" ]的名字不能为空！";
    /**
     * 检查配置文件
     */
    public void checkProperties() {
        String errormsg = null;
        if (ValidateUtilExt.isNullOrEmpty(kettleContextProperties.getKettleRepoPath())) {
            errormsg = "配置错误：kettle Repo 路径 不能为空";
        } else if (ValidateUtilExt.isNullOrEmpty(kettleContextProperties.getKettleRepoId())) {
            errormsg = "配置错误：kettle Repo Id 不能为空";
        }else if (ValidateUtilExt.isNullOrEmpty(kettleContextProperties.getKettleRepoName())) {
            errormsg = "配置错误：kettle Repo 名称 不能为空";
        }else if(ValidateUtilExt.isNotNullOrEmpty(kettleContextProperties.getKettleMap())){
            Map<KettleTypeEnum, KettleContextInfo> kettleMap = kettleContextProperties.getKettleMap();
            for (KettleTypeEnum info:kettleMap.keySet()){
                if(ValidateUtilExt.isNullOrEmpty(kettleMap.get(info).getName())){
                    errormsg=MSG_HEAD+info.getValue()+MSG_TAIL;
                    break;
                }
            }
        }
        if (ValidateUtilExt.isNotNullOrEmpty(errormsg)) {
            throw new ErrorMessageException(ErrorCode.CheckError, errormsg);
        }
    }
}
