package net.engining.redis.autoconfigure;

import net.engining.pg.config.RedisContextConfig;
import net.engining.pg.redis.aop.LockAop;
import net.engining.pg.redis.aop.RedisMqAop;
import net.engining.pg.redis.connection.RedissonConnectionConfiguration;
import net.engining.pg.redis.connection.RedissonConnectionFactory;
import net.engining.pg.redis.operation.RedissonBinary;
import net.engining.pg.redis.operation.RedissonCollection;
import net.engining.pg.redis.operation.RedissonObject;
import net.engining.pg.redis.props.MultipleServerProperties;
import net.engining.pg.redis.props.RedissonCommonProperties;
import net.engining.pg.redis.props.SingleServerProperties;
import net.engining.pg.redis.utils.RedisUtil;
import net.engining.pg.support.core.exception.ErrorCode;
import net.engining.pg.support.core.exception.ErrorMessageException;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.MasterSlaveServersConfig;
import org.redisson.config.ReplicatedServersConfig;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.redisson.connection.balancer.LoadBalancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;

/**
 * @author Eric Lu
 */
@Configuration
@EnableConfigurationProperties(value = RedissonCommonProperties.class)
@ConditionalOnClass(RedissonCommonProperties.class)
@ConditionalOnProperty(prefix = "pg.redisson", name = "enabled")
@Import({
        RedisContextConfig.class
})
public class RedissonAutoConfiguration {

    @Autowired
    private RedissonCommonProperties redissonProperties;

    @Bean
    @ConditionalOnMissingBean(LockAop.class)
    public LockAop lockAop() {
        return new LockAop();
    }

    @Bean
    @ConditionalOnMissingBean(RedisMqAop.class)
    public RedisMqAop mqAop() {
        return new RedisMqAop();
    }

    @Bean
    @ConditionalOnMissingBean(RedissonBinary.class)
    public RedissonBinary redissonBinary() {
        return new RedissonBinary();
    }

    @Bean
    @ConditionalOnMissingBean(RedissonObject.class)
    public RedissonObject redissonObject() {
        return new RedissonObject();
    }

    @Bean
    @ConditionalOnMissingBean(RedissonCollection.class)
    public RedissonCollection redissonCollection() {
        return new RedissonCollection();
    }

    /**
     * 使用Redisson构建RedisConnectionFactory，与Spring-data-redis整合
     */
    @Bean
    @ConditionalOnMissingBean({RedisConnectionFactory.class})
    public RedisConnectionFactory redisConnectionFactory(RedissonClient redisson) {
        return new RedissonConnectionFactory(redisson);
    }

    @Bean(name = RedisUtil.REDISSON_CLIENT, destroyMethod = "shutdown")
    @ConditionalOnMissingBean(RedissonClient.class)
    public RedissonClient redissonClient(RedissonConnectionConfiguration redissonConnectionConfiguration) {

        return Redisson.create(redissonConnectionConfiguration);
    }

    @Bean(name = RedisUtil.REDISSON_CONNECTION_CONFIGURATION)
    public RedissonConnectionConfiguration redissonConnectionConfiguration() throws MalformedURLException {

        RedissonConnectionConfiguration config = new RedissonConnectionConfiguration();
        try {
            config.setCodec((Codec) Class.forName(redissonProperties.getCodec()).newInstance());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        config.setTransportMode(redissonProperties.getTransportMode());
        if(redissonProperties.getThreads()!=null){
            config.setThreads(redissonProperties.getThreads());
        }
        if(redissonProperties.getNettyThreads()!=null){
            config.setNettyThreads(redissonProperties.getNettyThreads());
        }
        config.setReferenceEnabled(redissonProperties.getReferenceEnabled());
        config.setLockWatchdogTimeout(redissonProperties.getLockWatchdogTimeout());
        config.setKeepPubSubOrder(redissonProperties.getKeepPubSubOrder());
        config.setUseScriptCache(redissonProperties.getUseScriptCache());
        config.setMinCleanUpDelay(redissonProperties.getMinCleanUpDelay());
        config.setMaxCleanUpDelay(redissonProperties.getMaxCleanUpDelay());

        MultipleServerProperties multipleServerConfig = redissonProperties.getMultipleServerConfig();

        switch (redissonProperties.getModel()){
            case SINGLE:
                SingleServerConfig singleServerConfig = config.useSingleServer();
                SingleServerProperties singleServerProperties = redissonProperties.getSingleServerProperties();
                singleServerConfig.setAddress(prefixAddress(singleServerProperties.getAddress()));
                singleServerConfig.setConnectionMinimumIdleSize(singleServerProperties.getConnectionMinimumIdleSize());
                singleServerConfig.setConnectionPoolSize(singleServerProperties.getConnectionPoolSize());
                singleServerConfig.setDatabase(singleServerProperties.getDatabase());
                singleServerConfig.setDnsMonitoringInterval(singleServerProperties.getDnsMonitoringInterval());
                singleServerConfig.setSubscriptionConnectionMinimumIdleSize(singleServerProperties.getSubscriptionConnectionMinimumIdleSize());
                singleServerConfig.setSubscriptionConnectionPoolSize(singleServerProperties.getSubscriptionConnectionPoolSize());
                singleServerConfig.setClientName(redissonProperties.getClientName());
                singleServerConfig.setConnectTimeout(redissonProperties.getConnectTimeout());
                singleServerConfig.setIdleConnectionTimeout(redissonProperties.getIdleConnectionTimeout());
                singleServerConfig.setKeepAlive(redissonProperties.getKeepAlive());
                singleServerConfig.setPassword(redissonProperties.getPassword());
                singleServerConfig.setPingConnectionInterval(redissonProperties.getPingConnectionInterval());
                singleServerConfig.setRetryAttempts(redissonProperties.getRetryAttempts());
                singleServerConfig.setRetryInterval(redissonProperties.getRetryInterval());
                singleServerConfig.setSslEnableEndpointIdentification(redissonProperties.getSslEnableEndpointIdentification());
                if (ValidateUtilExt.isNotNullOrEmpty(redissonProperties.getSslKeystore())){
                    singleServerConfig.setSslKeystore(redissonProperties.getSslKeystore().toURL());
                }
                singleServerConfig.setSslKeystorePassword(redissonProperties.getSslKeystorePassword());
                singleServerConfig.setSslProvider(redissonProperties.getSslProvider());
                if (ValidateUtilExt.isNotNullOrEmpty(redissonProperties.getSslTruststore())){
                    singleServerConfig.setSslTruststore(redissonProperties.getSslTruststore().toURL());
                }
                singleServerConfig.setSslTruststorePassword(redissonProperties.getSslTruststorePassword());
                singleServerConfig.setSubscriptionsPerConnection(redissonProperties.getSubscriptionsPerConnection());
                singleServerConfig.setTcpNoDelay(redissonProperties.getTcpNoDelay());
                singleServerConfig.setTimeout(redissonProperties.getTimeout());
                config.setSingleServerConfig(singleServerConfig);
                break;
            case CLUSTER:
                ClusterServersConfig clusterServersConfig = config.useClusterServers();
                clusterServersConfig.setScanInterval(multipleServerConfig.getScanInterval());
                clusterServersConfig.setSlaveConnectionMinimumIdleSize(multipleServerConfig.getSlaveConnectionMinimumIdleSize());
                clusterServersConfig.setSlaveConnectionPoolSize(multipleServerConfig.getSlaveConnectionPoolSize());
                clusterServersConfig.setFailedSlaveReconnectionInterval(multipleServerConfig.getFailedSlaveReconnectionInterval());
                clusterServersConfig.setFailedSlaveCheckInterval(multipleServerConfig.getFailedSlaveCheckInterval());
                clusterServersConfig.setMasterConnectionMinimumIdleSize(multipleServerConfig.getMasterConnectionMinimumIdleSize());
                clusterServersConfig.setMasterConnectionPoolSize(multipleServerConfig.getMasterConnectionPoolSize());
                clusterServersConfig.setReadMode(multipleServerConfig.getReadMode());
                clusterServersConfig.setSubscriptionMode(multipleServerConfig.getSubscriptionMode());
                clusterServersConfig.setSubscriptionConnectionMinimumIdleSize(multipleServerConfig.getSubscriptionConnectionMinimumIdleSize());
                clusterServersConfig.setSubscriptionConnectionPoolSize(multipleServerConfig.getSubscriptionConnectionPoolSize());
                clusterServersConfig.setDnsMonitoringInterval(multipleServerConfig.getDnsMonitoringInterval());
                try {
                    clusterServersConfig.setLoadBalancer((LoadBalancer) Class.forName(multipleServerConfig.getLoadBalancer()).newInstance());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                for (String nodeAddress : multipleServerConfig.getNodeAddresses()) {
                    clusterServersConfig.addNodeAddress(prefixAddress(nodeAddress));
                }
                clusterServersConfig.setClientName(redissonProperties.getClientName());
                clusterServersConfig.setConnectTimeout(redissonProperties.getConnectTimeout());
                clusterServersConfig.setIdleConnectionTimeout(redissonProperties.getIdleConnectionTimeout());
                clusterServersConfig.setKeepAlive(redissonProperties.getKeepAlive());
                clusterServersConfig.setPassword(redissonProperties.getPassword());
                clusterServersConfig.setPingConnectionInterval(redissonProperties.getPingConnectionInterval());
                clusterServersConfig.setRetryAttempts(redissonProperties.getRetryAttempts());
                clusterServersConfig.setRetryInterval(redissonProperties.getRetryInterval());
                clusterServersConfig.setSslEnableEndpointIdentification(redissonProperties.getSslEnableEndpointIdentification());
                if (ValidateUtilExt.isNotNullOrEmpty(redissonProperties.getSslKeystore())){
                    clusterServersConfig.setSslKeystore(redissonProperties.getSslKeystore().toURL());
                }
                clusterServersConfig.setSslKeystorePassword(redissonProperties.getSslKeystorePassword());
                clusterServersConfig.setSslProvider(redissonProperties.getSslProvider());
                if (ValidateUtilExt.isNotNullOrEmpty(redissonProperties.getSslTruststore())){
                    clusterServersConfig.setSslKeystore(redissonProperties.getSslTruststore().toURL());
                }
                clusterServersConfig.setSslTruststorePassword(redissonProperties.getSslTruststorePassword());
                clusterServersConfig.setSubscriptionsPerConnection(redissonProperties.getSubscriptionsPerConnection());
                clusterServersConfig.setTcpNoDelay(redissonProperties.getTcpNoDelay());
                clusterServersConfig.setTimeout(redissonProperties.getTimeout());
                config.setClusterServersConfig(clusterServersConfig);
                break;
            case SENTINEL:
                SentinelServersConfig sentinelServersConfig = config.useSentinelServers();
                sentinelServersConfig.setDatabase(multipleServerConfig.getDatabase());
                sentinelServersConfig.setMasterName(multipleServerConfig.getMasterName());
                sentinelServersConfig.setScanInterval(multipleServerConfig.getScanInterval());
                sentinelServersConfig.setSlaveConnectionMinimumIdleSize(multipleServerConfig.getSlaveConnectionMinimumIdleSize());
                sentinelServersConfig.setSlaveConnectionPoolSize(multipleServerConfig.getSlaveConnectionPoolSize());
                sentinelServersConfig.setFailedSlaveReconnectionInterval(multipleServerConfig.getFailedSlaveReconnectionInterval());
                sentinelServersConfig.setFailedSlaveCheckInterval(multipleServerConfig.getFailedSlaveCheckInterval());
                sentinelServersConfig.setMasterConnectionMinimumIdleSize(multipleServerConfig.getMasterConnectionMinimumIdleSize());
                sentinelServersConfig.setMasterConnectionPoolSize(multipleServerConfig.getMasterConnectionPoolSize());
                sentinelServersConfig.setReadMode(multipleServerConfig.getReadMode());
                sentinelServersConfig.setSubscriptionMode(multipleServerConfig.getSubscriptionMode());
                sentinelServersConfig.setSubscriptionConnectionMinimumIdleSize(multipleServerConfig.getSubscriptionConnectionMinimumIdleSize());
                sentinelServersConfig.setSubscriptionConnectionPoolSize(multipleServerConfig.getSubscriptionConnectionPoolSize());
                sentinelServersConfig.setDnsMonitoringInterval(multipleServerConfig.getDnsMonitoringInterval());
                try {
                    sentinelServersConfig.setLoadBalancer((LoadBalancer) Class.forName(multipleServerConfig.getLoadBalancer()).newInstance());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                for (String nodeAddress : multipleServerConfig.getNodeAddresses()) {
                    sentinelServersConfig.addSentinelAddress(prefixAddress(nodeAddress));
                }
                sentinelServersConfig.setClientName(redissonProperties.getClientName());
                sentinelServersConfig.setConnectTimeout(redissonProperties.getConnectTimeout());
                sentinelServersConfig.setIdleConnectionTimeout(redissonProperties.getIdleConnectionTimeout());
                sentinelServersConfig.setKeepAlive(redissonProperties.getKeepAlive());
                sentinelServersConfig.setPassword(redissonProperties.getPassword());
                sentinelServersConfig.setPingConnectionInterval(redissonProperties.getPingConnectionInterval());
                sentinelServersConfig.setRetryAttempts(redissonProperties.getRetryAttempts());
                sentinelServersConfig.setRetryInterval(redissonProperties.getRetryInterval());
                sentinelServersConfig.setSslEnableEndpointIdentification(redissonProperties.getSslEnableEndpointIdentification());
                if (ValidateUtilExt.isNotNullOrEmpty(redissonProperties.getSslKeystore())){
                    sentinelServersConfig.setSslKeystore(redissonProperties.getSslKeystore().toURL());
                }
                sentinelServersConfig.setSslKeystorePassword(redissonProperties.getSslKeystorePassword());
                sentinelServersConfig.setSslProvider(redissonProperties.getSslProvider());
                if (ValidateUtilExt.isNotNullOrEmpty(redissonProperties.getSslTruststore())){
                    sentinelServersConfig.setSslTruststore(redissonProperties.getSslTruststore().toURL());
                }
                sentinelServersConfig.setSslTruststorePassword(redissonProperties.getSslTruststorePassword());
                sentinelServersConfig.setSubscriptionsPerConnection(redissonProperties.getSubscriptionsPerConnection());
                sentinelServersConfig.setTcpNoDelay(redissonProperties.getTcpNoDelay());
                sentinelServersConfig.setTimeout(redissonProperties.getTimeout());
                config.setSentinelServersConfig(sentinelServersConfig);
                break;
            case REPLICATED:
                ReplicatedServersConfig replicatedServersConfig = config.useReplicatedServers();
                replicatedServersConfig.setDatabase(multipleServerConfig.getDatabase());
                replicatedServersConfig.setScanInterval(multipleServerConfig.getScanInterval());
                replicatedServersConfig.setSlaveConnectionMinimumIdleSize(multipleServerConfig.getSlaveConnectionMinimumIdleSize());
                replicatedServersConfig.setSlaveConnectionPoolSize(multipleServerConfig.getSlaveConnectionPoolSize());
                replicatedServersConfig.setFailedSlaveReconnectionInterval(multipleServerConfig.getFailedSlaveReconnectionInterval());
                replicatedServersConfig.setFailedSlaveCheckInterval(multipleServerConfig.getFailedSlaveCheckInterval());
                replicatedServersConfig.setMasterConnectionMinimumIdleSize(multipleServerConfig.getMasterConnectionMinimumIdleSize());
                replicatedServersConfig.setMasterConnectionPoolSize(multipleServerConfig.getMasterConnectionPoolSize());
                replicatedServersConfig.setReadMode(multipleServerConfig.getReadMode());
                replicatedServersConfig.setSubscriptionMode(multipleServerConfig.getSubscriptionMode());
                replicatedServersConfig.setSubscriptionConnectionMinimumIdleSize(multipleServerConfig.getSubscriptionConnectionMinimumIdleSize());
                replicatedServersConfig.setSubscriptionConnectionPoolSize(multipleServerConfig.getSubscriptionConnectionPoolSize());
                replicatedServersConfig.setDnsMonitoringInterval(multipleServerConfig.getDnsMonitoringInterval());
                try {
                    replicatedServersConfig.setLoadBalancer((LoadBalancer) Class.forName(multipleServerConfig.getLoadBalancer()).newInstance());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                for (String nodeAddress : multipleServerConfig.getNodeAddresses()) {
                    replicatedServersConfig.addNodeAddress(prefixAddress(nodeAddress));
                }
                replicatedServersConfig.setClientName(redissonProperties.getClientName());
                replicatedServersConfig.setConnectTimeout(redissonProperties.getConnectTimeout());
                replicatedServersConfig.setIdleConnectionTimeout(redissonProperties.getIdleConnectionTimeout());
                replicatedServersConfig.setKeepAlive(redissonProperties.getKeepAlive());
                replicatedServersConfig.setPassword(redissonProperties.getPassword());
                replicatedServersConfig.setPingConnectionInterval(redissonProperties.getPingConnectionInterval());
                replicatedServersConfig.setRetryAttempts(redissonProperties.getRetryAttempts());
                replicatedServersConfig.setRetryInterval(redissonProperties.getRetryInterval());
                replicatedServersConfig.setSslEnableEndpointIdentification(redissonProperties.getSslEnableEndpointIdentification());
                if (ValidateUtilExt.isNotNullOrEmpty(redissonProperties.getSslKeystore())){
                    replicatedServersConfig.setSslKeystore(redissonProperties.getSslKeystore().toURL());
                }
                replicatedServersConfig.setSslKeystorePassword(redissonProperties.getSslKeystorePassword());
                replicatedServersConfig.setSslProvider(redissonProperties.getSslProvider());
                if (ValidateUtilExt.isNotNullOrEmpty(redissonProperties.getSslTruststore())){
                    replicatedServersConfig.setSslTruststore(redissonProperties.getSslTruststore().toURL());
                }
                replicatedServersConfig.setSslTruststorePassword(redissonProperties.getSslTruststorePassword());
                replicatedServersConfig.setSubscriptionsPerConnection(redissonProperties.getSubscriptionsPerConnection());
                replicatedServersConfig.setTcpNoDelay(redissonProperties.getTcpNoDelay());
                replicatedServersConfig.setTimeout(redissonProperties.getTimeout());
                config.setReplicatedServersConfig(replicatedServersConfig);
                break;
            case MASTERSLAVE:
                MasterSlaveServersConfig masterSlaveServersConfig = config.useMasterSlaveServers();
                masterSlaveServersConfig.setDatabase(multipleServerConfig.getDatabase());
                masterSlaveServersConfig.setSlaveConnectionMinimumIdleSize(multipleServerConfig.getSlaveConnectionMinimumIdleSize());
                masterSlaveServersConfig.setSlaveConnectionPoolSize(multipleServerConfig.getSlaveConnectionPoolSize());
                masterSlaveServersConfig.setFailedSlaveReconnectionInterval(multipleServerConfig.getFailedSlaveReconnectionInterval());
                masterSlaveServersConfig.setFailedSlaveCheckInterval(multipleServerConfig.getFailedSlaveCheckInterval());
                masterSlaveServersConfig.setMasterConnectionMinimumIdleSize(multipleServerConfig.getMasterConnectionMinimumIdleSize());
                masterSlaveServersConfig.setMasterConnectionPoolSize(multipleServerConfig.getMasterConnectionPoolSize());
                masterSlaveServersConfig.setReadMode(multipleServerConfig.getReadMode());
                masterSlaveServersConfig.setSubscriptionMode(multipleServerConfig.getSubscriptionMode());
                masterSlaveServersConfig.setSubscriptionConnectionMinimumIdleSize(multipleServerConfig.getSubscriptionConnectionMinimumIdleSize());
                masterSlaveServersConfig.setSubscriptionConnectionPoolSize(multipleServerConfig.getSubscriptionConnectionPoolSize());
                masterSlaveServersConfig.setDnsMonitoringInterval(multipleServerConfig.getDnsMonitoringInterval());
                try {
                    masterSlaveServersConfig.setLoadBalancer((LoadBalancer) Class.forName(multipleServerConfig.getLoadBalancer()).newInstance());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                int index=0;
                for (String nodeAddress : multipleServerConfig.getNodeAddresses()) {
                    if(index++==0){
                        masterSlaveServersConfig.setMasterAddress(prefixAddress(nodeAddress));
                    }else{
                        masterSlaveServersConfig.addSlaveAddress(prefixAddress(nodeAddress));
                    }
                }
                masterSlaveServersConfig.setClientName(redissonProperties.getClientName());
                masterSlaveServersConfig.setConnectTimeout(redissonProperties.getConnectTimeout());
                masterSlaveServersConfig.setIdleConnectionTimeout(redissonProperties.getIdleConnectionTimeout());
                masterSlaveServersConfig.setKeepAlive(redissonProperties.getKeepAlive());
                masterSlaveServersConfig.setPassword(redissonProperties.getPassword());
                masterSlaveServersConfig.setPingConnectionInterval(redissonProperties.getPingConnectionInterval());
                masterSlaveServersConfig.setRetryAttempts(redissonProperties.getRetryAttempts());
                masterSlaveServersConfig.setRetryInterval(redissonProperties.getRetryInterval());
                masterSlaveServersConfig.setSslEnableEndpointIdentification(redissonProperties.getSslEnableEndpointIdentification());
                if (ValidateUtilExt.isNotNullOrEmpty(redissonProperties.getSslKeystore())){
                    masterSlaveServersConfig.setSslKeystore(redissonProperties.getSslKeystore().toURL());
                }
                masterSlaveServersConfig.setSslKeystorePassword(redissonProperties.getSslKeystorePassword());
                masterSlaveServersConfig.setSslProvider(redissonProperties.getSslProvider());
                if (ValidateUtilExt.isNotNullOrEmpty(redissonProperties.getSslTruststore())){
                    masterSlaveServersConfig.setSslTruststore(redissonProperties.getSslTruststore().toURL());
                }
                masterSlaveServersConfig.setSslTruststorePassword(redissonProperties.getSslTruststorePassword());
                masterSlaveServersConfig.setSubscriptionsPerConnection(redissonProperties.getSubscriptionsPerConnection());
                masterSlaveServersConfig.setTcpNoDelay(redissonProperties.getTcpNoDelay());
                masterSlaveServersConfig.setTimeout(redissonProperties.getTimeout());
                config.setMasterSlaveServersConfig(masterSlaveServersConfig);
                break;
            default:
                throw new ErrorMessageException(ErrorCode.Other, "not support this redis server mode!!!");
        }

        return config;
    }

    private String prefixAddress(String address){
        if(!StringUtils.isEmpty(address)&&!address.startsWith("redis")){
           return "redis://"+address;
        }
        return address;
    }

}
