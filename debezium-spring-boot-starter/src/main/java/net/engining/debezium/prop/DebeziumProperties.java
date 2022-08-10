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
