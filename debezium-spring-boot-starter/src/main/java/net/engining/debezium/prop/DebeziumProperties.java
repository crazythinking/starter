package net.engining.debezium.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用配置项
 *
 * @author : Eric Lu
 * @version :
 * @date : 2021-08-11 14:13
 * @since :
 **/
@ConfigurationProperties("pg.debezium")
public class DebeziumProperties {

    private boolean enabled = true;

    private String dataPath;

    /**
     * 是否启用 JRaft 作为 CDC 的Offset和Snapshot存储
     */
    private boolean enabledJRaft = false;

    /**
     * 是否启用 Kafka 作为 CDC event 的消息来源；
     * 注意：该能力是与内嵌 Debezium Engine 的能力互斥的，不能同时启用；打开该能力后，内嵌 Debezium Engine 的能力将被关闭；
     */
    private boolean enabledKafkaCdc = false;

    private Map<String, Map<String, String>> namedProperties = new HashMap<>();

    /**
     * 异步线程池的核心线程数大小
     */
    private int asyncExcutorColePoolSize = 5;

    /**
     * 异步线程池的最大线程数大小
     */
    private int asyncExcutorMaxPoolSize = 5;

    /**
     * 异步线程池的队列大小
     */
    private int asyncExcutorQueueCapacity = 10;

    public boolean isEnabledKafkaCdc() {
        return enabledKafkaCdc;
    }

    public void setEnabledKafkaCdc(boolean enabledKafkaCdc) {
        this.enabledKafkaCdc = enabledKafkaCdc;
    }

    public boolean isEnabledJRaft() {
        return enabledJRaft;
    }

    public void setEnabledJRaft(boolean enabledJRaft) {
        this.enabledJRaft = enabledJRaft;
    }

    public int getAsyncExcutorColePoolSize() {
        return asyncExcutorColePoolSize;
    }

    public void setAsyncExcutorColePoolSize(int asyncExcutorColePoolSize) {
        this.asyncExcutorColePoolSize = asyncExcutorColePoolSize;
    }

    public int getAsyncExcutorMaxPoolSize() {
        return asyncExcutorMaxPoolSize;
    }

    public void setAsyncExcutorMaxPoolSize(int asyncExcutorMaxPoolSize) {
        this.asyncExcutorMaxPoolSize = asyncExcutorMaxPoolSize;
    }

    public int getAsyncExcutorQueueCapacity() {
        return asyncExcutorQueueCapacity;
    }

    public void setAsyncExcutorQueueCapacity(int asyncExcutorQueueCapacity) {
        this.asyncExcutorQueueCapacity = asyncExcutorQueueCapacity;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, Map<String, String>> getNamedProperties() {
        return namedProperties;
    }

    public void setNamedProperties(Map<String, Map<String, String>> namedProperties) {
        this.namedProperties = namedProperties;
    }
}
