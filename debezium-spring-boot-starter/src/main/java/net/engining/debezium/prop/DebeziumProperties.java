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
