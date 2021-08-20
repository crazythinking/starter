package net.engining.debezium.autoconfigure;

import cn.hutool.core.util.StrUtil;
import io.debezium.engine.DebeziumEngine;
import net.engining.pg.support.utils.ExceptionUtilsExt;
import net.engining.pg.support.utils.ThreadUtilsExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(DebeziumServerBootstrap.class);

    private String suffix;

    protected boolean running = false;

    private final Executor executor;

    private DebeziumEngine<?> debeziumEngine;

    public DebeziumServerBootstrap(String suffix) {
        this.suffix = suffix;
        this.executor = ThreadUtilsExt.newSingleThreadExecutor(
                "DBZ-Engine-"+ this.suffix + StrUtil.DASHED,
                false
        );
    }

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
        try {
            executor.execute(debeziumEngine);
        } finally {
            running = true;
        }
    }

    @Override
    public void stop() {
        try {
            debeziumEngine.close();
        } catch (IOException e) {
            LOGGER.error("DebeziumEngine close failed: {}", e.getMessage());
            ExceptionUtilsExt.dump(e);
        } finally {
            running = false;
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(debeziumEngine, "debeziumEngine must not be null");
    }
}
