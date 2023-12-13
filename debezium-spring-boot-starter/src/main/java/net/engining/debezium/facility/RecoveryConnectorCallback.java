package net.engining.debezium.facility;

import io.debezium.engine.DebeziumEngine;
import io.debezium.pipeline.ErrorHandler;
import net.engining.debezium.autoconfigure.DebeziumAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 主动尝试恢复Connector
 *
 * @author : Eric Lu
 * @version :
 * @since : 2023-12-13 10:04
 *
 * Deprecated: 由于Debezium connector 本身就存在retry的机制，所以这里的代码是多余的；{@link ErrorHandler}
 **/
@Deprecated
public class RecoveryConnectorCallback implements DebeziumEngine.ConnectorCallback {
    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RecoveryConnectorCallback.class);

    private final String key;

    private final int retryTimes;

    private int retryCount = 0;

    private final ApplicationContext applicationContext;

    public RecoveryConnectorCallback(String key, int retryTimes, ApplicationContext applicationContext) {
        this.key = key;
        this.applicationContext = applicationContext;
        this.retryTimes = retryTimes;
    }

    @Override
    public void connectorStopped() {
        try {
            // 每次Connector停止时，都会尝试回调该方法，最多尝试retryTimes次
            TimeUnit.SECONDS.sleep(retryCount * 2L);
            if (retryCount < retryTimes) {
                retryCount++;
                LOGGER.warn("DebeziumEngine connector {} stopped, try to restart it, retryCount: {}", key, retryCount);
                recoverConnector();
            } else {
                LOGGER.warn(
                        "DebeziumEngine connector {} stopped, retryCount: {}, exceed the maximum number of retries: {}, ",
                        key,
                        retryCount,
                        retryTimes
                );
            }
        } catch (Exception e) {
            LOGGER.error("DebeziumEngine connector {} stopped, try to restart it, retryCount: {}", key, retryCount, e);
        }
    }

    private void recoverConnector() {
        Map debeziumServerBootstrapMap = applicationContext.getBean(
                DebeziumAutoConfiguration.DEBEZIUM_SERVER_BOOTSTRAP_MAP, Map.class
        );
        DebeziumServerBootstrap debeziumServerBootstrap = (DebeziumServerBootstrap) debeziumServerBootstrapMap.get(key);

        if (debeziumServerBootstrap != null && debeziumServerBootstrap.isRunning()) {
            debeziumServerBootstrap.start();
        }
        LOGGER.warn("DebeziumEngine connector stopped, {} has bean restarted", key);
    }
}
