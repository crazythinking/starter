package net.engining.metrics.prop;

import com.google.common.collect.Maps;
import net.engining.pg.support.db.DbType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2022-08-31 16:38
 * @since :
 **/
@ConfigurationProperties("pg.metrics.debezium")
public class DebeziumMetricsProperties {

    /**
     * CDC Name：逻辑名，与database.server.name配置项一致
     */
    Map<DbType, List<String>> cdcDefinition = Maps.newHashMap();

    public Map<DbType, List<String>> getCdcDefinition() {
        return cdcDefinition;
    }

    public void setCdcDefinition(Map<DbType, List<String>> cdcDefinition) {
        this.cdcDefinition = cdcDefinition;
    }
}
