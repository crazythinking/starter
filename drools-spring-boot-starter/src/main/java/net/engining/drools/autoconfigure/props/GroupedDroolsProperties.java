package net.engining.drools.autoconfigure.props;

import org.kie.api.management.GAV;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-11-16 12:15
 * @since :
 **/
@ConfigurationProperties(prefix = "pg.drools")
public class GroupedDroolsProperties {

    /**
     * Is this starter enabled?
     */
    private boolean enabled = true;

    /**
     * 规则Assert部署的基础目录，各KieModule在其下构建子目录
     */
    private String basePath;

    /**
     * 是否开启监听器
     */
    private boolean monitor;

    /**
     * 自动更新规则
     */
    private boolean update;

    /**
     * 更新周期 - 单位：毫秒
     */
    private Long interval;

    /**
     * 是否开启规则
     */
    private boolean verifyRule;

    private Map<GAV, DroolsProperties> gavDroolsProperties;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public boolean isMonitor() {
        return monitor;
    }

    public void setMonitor(boolean monitor) {
        this.monitor = monitor;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public Long getInterval() {
        return interval;
    }

    public void setInterval(Long interval) {
        this.interval = interval;
    }

    public boolean isVerifyRule() {
        return verifyRule;
    }

    public void setVerifyRule(boolean verifyRule) {
        this.verifyRule = verifyRule;
    }

    public Map<GAV, DroolsProperties> getGavDroolsProperties() {
        return gavDroolsProperties;
    }

    public void setGavDroolsProperties(Map<GAV, DroolsProperties> gavDroolsProperties) {
        this.gavDroolsProperties = gavDroolsProperties;
    }
}
