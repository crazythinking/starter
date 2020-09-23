package net.engining.kettle.prop;

import net.engining.kettle.common.KettleContextInfo;
import net.engining.kettle.common.KettleLogLevel;
import net.engining.kettle.common.KettleType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Kettle 参数配置
 *
 * @author 陈宝
 * @version 1.0
 * @date 2020/9/23 14:42
 * @since 1.0
 */
@ConfigurationProperties("starter.kettle.config")
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
    private KettleLogLevel kettleLogLevel = KettleLogLevel.minimal;
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
    private Map<KettleType, KettleContextInfo> kettleMap;

    public Map<KettleType, KettleContextInfo> getKettleMap() {
        return kettleMap;
    }

    public void setKettleMap(Map<KettleType, KettleContextInfo> kettleMap) {
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

    public KettleLogLevel getKettleLogLevel() {
        return kettleLogLevel;
    }

    public void setKettleLogLevel(KettleLogLevel kettleLogLevel) {
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
