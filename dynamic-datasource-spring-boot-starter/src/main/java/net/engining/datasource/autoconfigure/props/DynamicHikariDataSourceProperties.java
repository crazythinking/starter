package net.engining.datasource.autoconfigure.props;

import com.zaxxer.hikari.HikariConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Map;

/**
 * @author Eric Lu
 * @create 2019-11-04 17:28
 **/
@ConfigurationProperties(prefix = "pg.datasource.dynamic.hikari")
public class DynamicHikariDataSourceProperties {

    private boolean enabled = true;

    @NestedConfigurationProperty
    private HikariConfig defaultCf;

    @NestedConfigurationProperty
    private Map<String, HikariConfig> cfMap;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public HikariConfig getDefaultCf() {
        return defaultCf;
    }

    public void setDefaultCf(HikariConfig defaultCf) {
        this.defaultCf = defaultCf;
    }

    public Map<String, HikariConfig> getCfMap() {
        return cfMap;
    }

    public void setCfMap(Map<String, HikariConfig> cfMap) {
        this.cfMap = cfMap;
    }
}
