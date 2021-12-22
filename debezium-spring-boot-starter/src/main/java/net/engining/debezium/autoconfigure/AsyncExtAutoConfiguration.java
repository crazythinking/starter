package net.engining.debezium.autoconfigure;

import net.engining.debezium.event.AbstractExtractedCdcEventListener;
import net.engining.debezium.prop.DebeziumProperties;
import net.engining.gm.config.props.GmCommonProperties;
import net.engining.pg.support.utils.ThreadUtilsExt;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-10-20 14:02
 * @since :
 **/
@Configuration
@ConditionalOnProperty(prefix = "pg.debezium.async", name = "enabled", matchIfMissing = true)
@AutoConfigureAfter({
        DebeziumAutoConfiguration.class
})
@EnableConfigurationProperties({
        DebeziumProperties.class,
        GmCommonProperties.class
})
@EnableAsync(mode = AdviceMode.ASPECTJ)
public class AsyncExtAutoConfiguration {


    @Bean(AbstractExtractedCdcEventListener.DEBEZIUM_EVENT_LISTENER)
    public AsyncTaskExecutor getAsyncExecutor(GmCommonProperties gmCommonProperties,
                                              DebeziumProperties debeziumProperties
    ) {
        return ThreadUtilsExt.newSpringThreadPoolTaskExecutor(
                debeziumProperties.getAsyncExcutorColePoolSize(),
                debeziumProperties.getAsyncExcutorMaxPoolSize(),
                debeziumProperties.getAsyncExcutorQueueCapacity(),
                AbstractExtractedCdcEventListener.DEBEZIUM_EVENT_LISTENER + "-",
                gmCommonProperties.getAwaitTerminationSeconds(),
                null,
                //线程池对拒绝任务（无线程可用）的处理策略:直接抛出java.util.concurrent.RejectedExecutionException异常
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

}
