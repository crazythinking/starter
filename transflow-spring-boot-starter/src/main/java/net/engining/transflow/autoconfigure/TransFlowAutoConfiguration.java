package net.engining.transflow.autoconfigure;

import net.engining.control.core.flow.FlowContext;
import net.engining.gm.config.FlowTransContextConfig;
import net.engining.gm.config.props.TransFlowProperties;
import net.engining.pg.support.utils.ThreadUtilsExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-02-22 17:25
 * @since :
 **/
@Configuration
@ConditionalOnProperty(prefix = "gm.trans-flow", name = "enabled", matchIfMissing = true)
@Import(value = {
        FlowTransContextConfig.class
})
@EnableConfigurationProperties({
        TransFlowProperties.class
})
public class TransFlowAutoConfiguration {

    @Autowired
    TransFlowProperties transFlowProperties;

    @Bean(FlowContext.TRANS_FLOW_EXECUTOR_SERVICE)
    public ExecutorService transFlowThreadPool() {
        return ThreadUtilsExt.newThreadPoolExecutor(
                transFlowProperties.getAsyncExcutorColePoolSize(),
                transFlowProperties.getAsyncExcutorMaxPoolSize(),
                transFlowProperties.getAsyncExcutorKeepAlive(),
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(transFlowProperties.getAsyncExcutorQueueCapacity()),
                "trans-flow-async-thread",
                false
        );
    }

}
