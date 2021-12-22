package net.engining.metrics.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-12-16 17:32
 * @since :
 **/
@ConfigurationProperties("pg.metrics.registry")
public class MetricsRegistryProperties {

    /**
     * StorageStepMeterRegistry需要被持久化的指标名称前缀
     */
    private List<String> stepMeterRegistryPrefixes;

    /**
     * StorageStepMeterRegistry存储触发的时间间隔(秒)
     */
    private long stepMeterRegistryInterval = 60L;

    /**
     * StoragePushMeterRegistry需要被持久化的指标名称前缀
     */
    private List<String> pushMeterRegistryPrefixes;

    /**
     * StoragePushMeterRegistry存储触发的时间间隔(秒)
     */
    private long pushMeterRegistryInterval = 60L;

    public List<String> getStepMeterRegistryPrefixes() {
        return stepMeterRegistryPrefixes;
    }

    public void setStepMeterRegistryPrefixes(List<String> stepMeterRegistryPrefixes) {
        this.stepMeterRegistryPrefixes = stepMeterRegistryPrefixes;
    }

    public long getStepMeterRegistryInterval() {
        return stepMeterRegistryInterval;
    }

    public void setStepMeterRegistryInterval(long stepMeterRegistryInterval) {
        this.stepMeterRegistryInterval = stepMeterRegistryInterval;
    }

    public List<String> getPushMeterRegistryPrefixes() {
        return pushMeterRegistryPrefixes;
    }

    public void setPushMeterRegistryPrefixes(List<String> pushMeterRegistryPrefixes) {
        this.pushMeterRegistryPrefixes = pushMeterRegistryPrefixes;
    }

    public long getPushMeterRegistryInterval() {
        return pushMeterRegistryInterval;
    }

    public void setPushMeterRegistryInterval(long pushMeterRegistryInterval) {
        this.pushMeterRegistryInterval = pushMeterRegistryInterval;
    }
}
