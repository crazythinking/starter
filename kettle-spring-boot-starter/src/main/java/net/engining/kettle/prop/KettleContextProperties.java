package net.engining.kettle.prop;

import com.google.common.collect.Maps;
import net.engining.kettle.common.KettleContextInfo;
import net.engining.kettle.common.KettleLogLevelEnum;
import net.engining.kettle.common.KettleTypeEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * Kettle 参数配置
 *
 * @author 陈宝
 * @version 1.0
 * @date 2020/9/23 14:42
 * @since 1.0
 */
@ConfigurationProperties("pg.kettle.config")
public class KettleContextProperties {
    /**
     * kettle Repo 路径
     */
    private String kettleRepoPath;
    /**
     * kettle Repo Id
     */
    private String kettleRepoId;
    /**
     * kettle Repo 名称
     */
    private String kettleRepoName;
    /**
     * kettle LogLevel  日志级别
     */
    private KettleLogLevelEnum kettleLogLevel = KettleLogLevelEnum.NOTHING;
    /**
     * 用户名
     */
    private String username = "";
    /**
     * 密码
     */
    private String password = "";
    /**
     * 执行配置
     */
    private Map<KettleTypeEnum, KettleContextInfo> kettleMap= Maps.newHashMap();
    /**
     * kettle 插件路径
     */
    private String kettlePluginPath = "./kettleplugins";

    public String getKettlePluginPath() {
        return kettlePluginPath;
    }

    public void setKettlePluginPath(String kettlePluginPath) {
        this.kettlePluginPath = kettlePluginPath;
    }

    public Map<KettleTypeEnum, KettleContextInfo> getKettleMap() {
        return kettleMap;
    }

    public void setKettleMap(Map<KettleTypeEnum, KettleContextInfo> kettleMap) {
        this.kettleMap = kettleMap;
    }


    public String getKettleRepoPath() {
        return kettleRepoPath;
    }

    public void setKettleRepoPath(String kettleRepoPath) {
        this.kettleRepoPath = kettleRepoPath;
    }

    public String getKettleRepoId() {
        return kettleRepoId;
    }

    public void setKettleRepoId(String kettleRepoId) {
        this.kettleRepoId = kettleRepoId;
    }

    public String getKettleRepoName() {
        return kettleRepoName;
    }

    public void setKettleRepoName(String kettleRepoName) {
        this.kettleRepoName = kettleRepoName;
    }

    public KettleLogLevelEnum getKettleLogLevel() {
        return kettleLogLevel;
    }

    public void setKettleLogLevel(KettleLogLevelEnum kettleLogLevel) {
        this.kettleLogLevel = kettleLogLevel;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
