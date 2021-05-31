package net.engining.zeebe.spring.client.ext.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Map;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-05-31 10:00
 * @since :
 **/
@ConfigurationProperties(prefix = "pg.zeebe.ext")
public class ZeebeExtProperties {

    @NestedConfigurationProperty
    private Map<String, EntityProperties> starterEntities;

    @NestedConfigurationProperty
    private Map<String, EntityProperties> workerEntities;

    public Map<String, EntityProperties> getStarterEntities() {
        return starterEntities;
    }

    public void setStarterEntities(Map<String, EntityProperties> starterEntities) {
        this.starterEntities = starterEntities;
    }

    public Map<String, EntityProperties> getWorkerEntities() {
        return workerEntities;
    }

    public void setWorkerEntities(Map<String, EntityProperties> workerEntities) {
        this.workerEntities = workerEntities;
    }

    @Override
    public String toString() {
        return "ZeebeExtProperties{" +
                "startersEntityProperties=" + starterEntities +
                ", workersEntityProperties=" + workerEntities +
                '}';
    }
}
