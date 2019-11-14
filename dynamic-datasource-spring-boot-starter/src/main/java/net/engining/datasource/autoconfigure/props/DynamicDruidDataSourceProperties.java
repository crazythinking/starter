package net.engining.datasource.autoconfigure.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Map;

/**
 * @author Eric Lu
 * @create 2019-11-04 17:28
 **/
@ConfigurationProperties(prefix = "pg.datasource.dynamic.druid")
public class DynamicDruidDataSourceProperties {

    private boolean enabled = false;

    @NestedConfigurationProperty
    private DruidDataSourceWrapper defaultDs;

    @NestedConfigurationProperty
    private Map<String, DruidDataSourceWrapper> dsMap;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public DruidDataSourceWrapper getDefaultDs() {
        return defaultDs;
    }

    public void setDefaultDs(DruidDataSourceWrapper defaultDs) {
        this.defaultDs = defaultDs;
    }

    public Map<String, DruidDataSourceWrapper> getDsMap() {
        return dsMap;
    }

    public void setDsMap(Map<String, DruidDataSourceWrapper> dsMap) {
        this.dsMap = dsMap;
    }
}
