package net.engining.debezium.facility;

import io.debezium.engine.DebeziumEngine;
import net.engining.debezium.autoconfigure.DebeziumAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * 在基于JRaft多活的情况下，当DebeziumEngine connector停止时，需要同步其上层容器{@link DebeziumServerBootstrap}的状态为stopped
 *
 * @author : Eric Lu
 * @version :
 * @since : 2023-12-13 16:47
 **/
public class StopBootstrapConnectorCallback implements DebeziumEngine.ConnectorCallback {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(StopBootstrapConnectorCallback.class);
    private final String key;

    private final ApplicationContext applicationContext;

    public StopBootstrapConnectorCallback(String key, ApplicationContext applicationContext) {
        this.key = key;
        this.applicationContext = applicationContext;
    }

    @Override
    public void connectorStopped() {
        Map debeziumServerBootstrapMap = applicationContext.getBean(
                DebeziumAutoConfiguration.DEBEZIUM_SERVER_BOOTSTRAP_MAP, Map.class
        );
        DebeziumServerBootstrap debeziumServerBootstrap = (DebeziumServerBootstrap) debeziumServerBootstrapMap.get(key);

        if (debeziumServerBootstrap != null && debeziumServerBootstrap.isRunning()) {
            debeziumServerBootstrap.stop();
        }
        LOGGER.warn("DebeziumEngine connector {} stopped", key);
    }
}
