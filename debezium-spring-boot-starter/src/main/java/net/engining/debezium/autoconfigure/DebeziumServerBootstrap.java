package net.engining.debezium.autoconfigure;

import io.debezium.engine.DebeziumEngine;
import net.engining.pg.support.utils.ThreadUtilsExt;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.concurrent.Executor;

/**
 * @author n1
 * @since 2021/6/2 10:45
 */
public class DebeziumServerBootstrap implements InitializingBean, SmartLifecycle {

    private final Executor executor = ThreadUtilsExt.newSingleThreadExecutor("Debezium-Engine-", true);
    private DebeziumEngine<?> debeziumEngine;

    public Executor getExecutor() {
        return executor;
    }

    public DebeziumEngine<?> getDebeziumEngine() {
        return debeziumEngine;
    }

    public void setDebeziumEngine(DebeziumEngine<?> debeziumEngine) {
        this.debeziumEngine = debeziumEngine;
    }

    @Override
    public void start() {
        executor.execute(debeziumEngine);
    }

    @Override
    public void stop() {
        try {
            debeziumEngine.close();
        } catch (IOException e) {
            //TODO
            e.printStackTrace();
        }
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(debeziumEngine, "debeziumEngine must not be null");
    }
}
