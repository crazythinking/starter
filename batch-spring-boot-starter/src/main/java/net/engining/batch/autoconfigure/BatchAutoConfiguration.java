package net.engining.batch.autoconfigure;

import net.engining.pg.batch.listener.BatchJobLoggedListener;
import net.engining.pg.batch.listener.BatchStepLoggedListener;
import net.engining.pg.batch.props.PgBatchTaskProperties;
import net.engining.pg.batch.runner.NonIncrementerableBatchJobRunner;
import net.engining.pg.batch.runner.RepeatableAnyTimeBatchJobRunner;
import net.engining.pg.batch.runner.Unrepeatable4OneTermBatchJobRunner;
import net.engining.pg.batch.sdk.NullItemWriter;
import net.engining.pg.batch.sdk.infrastructure.BatchJobCliRunner;
import org.springframework.batch.core.partition.support.DefaultStepExecutionAggregator;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2020-10-29 17:43
 * @since :
 **/
@Configuration
@AutoConfigureAfter(org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration.class)
@EnableConfigurationProperties({
        PgBatchTaskProperties.class,
})
@Import(value = {

})
@ConditionalOnProperty(prefix = "pg.batch", name = "enabled", matchIfMissing = true)
public class BatchAutoConfiguration {

    /**
     * 声明 BatchJobCliRunner
     *
     * @return BatchJobCliRunner
     */
    @Bean
    @ConditionalOnMissingBean
    public BatchJobCliRunner batchJobCliRunner() {
        return new BatchJobCliRunner();
    }

    /**
     * 声明 SimpleCompletionPolicy
     * @return SimpleCompletionPolicy
     */
    @Bean
    @ConditionalOnMissingBean
    public SimpleCompletionPolicy simpleCompletionPolicy(PgBatchTaskProperties batchTaskProperties) {
        return new SimpleCompletionPolicy(batchTaskProperties.getDefaultCommitInterval());
    }

    /**
     * 声明 BatchJobLoggedListener
     * @return BatchJobLoggedListener
     */
    @Bean
    @ConditionalOnMissingBean
    public BatchJobLoggedListener batchJobLoggedListener() {
        return new BatchJobLoggedListener();
    }

    /**
     * 声明 BatchStepLoggedListener
     * @return BatchStepLoggedListener
     */
    @Bean
    @ConditionalOnMissingBean
    public BatchStepLoggedListener batchStepLoggedListener() {
        return new BatchStepLoggedListener();
    }

    ///**
    // * 声明 SimpleJobOperator
    // * @return SimpleJobOperator
    // */
    //@Bean
    //@ConditionalOnMissingBean
    //public SimpleJobOperator simpleJobOperator() {
    //    SimpleJobOperator simpleJobOperator = new SimpleJobOperator();
    //    simpleJobOperator.setJobExplorer(jobExplorer);
    //    simpleJobOperator.setJobLauncher(jobLauncher);
    //    simpleJobOperator.setJobRegistry(jobRegistry);
    //    simpleJobOperator.setJobRepository(jobRepository);
    //    return simpleJobOperator;
    //}

    /**
     * 声明 NonIncrementerableBatchJobRunner
     * @return NonIncrementerableBatchJobRunner
     */
    @Bean
    @ConditionalOnMissingBean
    public NonIncrementerableBatchJobRunner nonIncrementerBatchJobRunner(){
        return new NonIncrementerableBatchJobRunner();
    }

    /**
     * 单个业务周期内不可重复执行的 Batch Job Runner
     *
     * @return 批量指定
     */
    @Bean
    @ConditionalOnMissingBean
    public Unrepeatable4OneTermBatchJobRunner unrepeatable4OneTermBatchJobRunner() {
        return new Unrepeatable4OneTermBatchJobRunner();
    }

    /**
     * 任何业务周期内可重复执行的 Batch Job Runner
     *
     * @return 批量执行
     */
    @Bean
    @ConditionalOnMissingBean
    public RepeatableAnyTimeBatchJobRunner repeatableAnyTimeBatchJobRunner() {
        return new RepeatableAnyTimeBatchJobRunner();
    }

    /**
     * 声明 DefaultStepExecutionAggregator
     * @return DefaultStepExecutionAggregator
     */
    @Bean
    @ConditionalOnMissingBean
    public DefaultStepExecutionAggregator defaultStepExecutionAggregator() {
        return new DefaultStepExecutionAggregator();
    }

    /**
     * 声明 NullItemWriter
     * @return NullItemWriter
     */
    @Bean
    @ConditionalOnMissingBean
    public NullItemWriter nullItemWriter(){
        return new NullItemWriter();
    }

}
