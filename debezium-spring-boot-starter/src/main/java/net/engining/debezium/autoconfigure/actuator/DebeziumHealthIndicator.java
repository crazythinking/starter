package net.engining.debezium.autoconfigure.actuator;

import net.engining.pg.support.db.DbType;
import org.springframework.boot.actuate.health.HealthIndicator;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2022-09-01 15:45
 * @since :
 **/
public interface DebeziumHealthIndicator extends HealthIndicator {

    String getName();

    DbType getDbType();

}
