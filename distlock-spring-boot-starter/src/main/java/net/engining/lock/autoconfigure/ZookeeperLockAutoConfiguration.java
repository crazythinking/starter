package net.engining.lock.autoconfigure;

import net.engining.lock.condition.ZookeeperCondition;
import net.engining.lock.excutor.ZookeeperLockExecutor;
import net.engining.pg.lock.props.ZookeeperProperties;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * Zookeeper锁自动配置器
 *
 * @author zengzhihong
 */
@Configuration
@ConditionalOnClass(CuratorFramework.class)
@Conditional(ZookeeperCondition.class)
@EnableConfigurationProperties({
        ZookeeperProperties.class
})
public class ZookeeperLockAutoConfiguration {

    @Autowired
    ZookeeperProperties zookeeperProperties;

    @Bean(initMethod = "start", destroyMethod = "close")
    @ConditionalOnMissingBean(CuratorFramework.class)
    public CuratorFramework curatorFramework() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(
                zookeeperProperties.getBaseSleepTimeMs(),
                zookeeperProperties.getMaxRetries()
        );
        return CuratorFrameworkFactory.builder()
                .connectString(zookeeperProperties.getZkServers())
                .sessionTimeoutMs(zookeeperProperties.getSessionTimeout())
                .connectionTimeoutMs(zookeeperProperties.getConnectionTimeout())
                .retryPolicy(retryPolicy)
                .build();
    }

    @Bean
    @Order(300)
    public ZookeeperLockExecutor zookeeperLockExecutor(CuratorFramework curatorFramework) {
        return new ZookeeperLockExecutor(curatorFramework);
    }
}