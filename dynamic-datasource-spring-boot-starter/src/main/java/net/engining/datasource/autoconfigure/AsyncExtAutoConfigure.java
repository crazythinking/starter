package net.engining.datasource.autoconfigure;

import net.engining.datasource.autoconfigure.prop.DdProperties;
import net.engining.datasource.autoconfigure.support.Utils;
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
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-10-20 14:02
 * @since :
 **/
@Configuration
@ConditionalOnProperty(prefix = "pg.datasource.dynamic.async", name = "enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter({
        DynamicDataSourceAutoConfigure.class,
        DynamicDruidDataSourceAutoConfigure.class,
        ShardingJdbcAutoConfiguration.class
})
@EnableConfigurationProperties({
        GmCommonProperties.class,
        DdProperties.class
})
@EnableAsync(mode = AdviceMode.ASPECTJ)
public class AsyncExtAutoConfigure {

    @Bean(Utils.DB_EVENT_LISTENER)
    public AsyncTaskExecutor getAsyncExecutor(GmCommonProperties gmCommonProperties, DdProperties ddProperties) {
        return ThreadUtilsExt.newSpringThreadPoolTaskExecutor(
                ddProperties.getAsyncExcutorColePoolSize(),
                ddProperties.getAsyncExcutorMaxPoolSize(),
                ddProperties.getAsyncExcutorQueueCapacity(),
                Utils.DB_EVENT_LISTENER + "-",
                gmCommonProperties.getAwaitTerminationSeconds(),
                null,
                //线程池对拒绝任务（无线程可用）的处理策略:直接抛出java.util.concurrent.RejectedExecutionException异常
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

}
