package net.engining.datasource.autoconfigure;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.engining.gm.config.AsyncExtContextConfig;
import net.engining.gm.config.props.GmCommonProperties;
import net.engining.pg.support.db.DbType;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.apache.shardingsphere.core.yaml.swapper.MasterSlaveRuleConfigurationYamlSwapper;
import org.apache.shardingsphere.core.yaml.swapper.ShardingRuleConfigurationYamlSwapper;
import org.apache.shardingsphere.core.yaml.swapper.impl.ShadowRuleConfigurationYamlSwapper;
import org.apache.shardingsphere.encrypt.yaml.swapper.EncryptRuleConfigurationYamlSwapper;
import org.apache.shardingsphere.shardingjdbc.api.EncryptDataSourceFactory;
import org.apache.shardingsphere.shardingjdbc.api.MasterSlaveDataSourceFactory;
import org.apache.shardingsphere.shardingjdbc.api.ShadowDataSourceFactory;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.apache.shardingsphere.shardingjdbc.spring.boot.common.SpringBootPropertiesConfigurationProperties;
import org.apache.shardingsphere.shardingjdbc.spring.boot.encrypt.EncryptRuleCondition;
import org.apache.shardingsphere.shardingjdbc.spring.boot.encrypt.SpringBootEncryptRuleConfigurationProperties;
import org.apache.shardingsphere.shardingjdbc.spring.boot.masterslave.MasterSlaveRuleCondition;
import org.apache.shardingsphere.shardingjdbc.spring.boot.masterslave.SpringBootMasterSlaveRuleConfigurationProperties;
import org.apache.shardingsphere.shardingjdbc.spring.boot.shadow.ShadowRuleCondition;
import org.apache.shardingsphere.shardingjdbc.spring.boot.shadow.SpringBootShadowRuleConfigurationProperties;
import org.apache.shardingsphere.shardingjdbc.spring.boot.sharding.ShardingRuleCondition;
import org.apache.shardingsphere.shardingjdbc.spring.boot.sharding.SpringBootShardingRuleConfigurationProperties;
import org.apache.shardingsphere.spring.boot.datasource.DataSourcePropertiesSetterHolder;
import org.apache.shardingsphere.spring.boot.util.DataSourceUtil;
import org.apache.shardingsphere.spring.boot.util.PropertyUtil;
import org.apache.shardingsphere.transaction.spring.ShardingTransactionTypeScanner;
import org.apache.shardingsphere.underlying.common.config.inline.InlineExpressionParser;
import org.apache.shardingsphere.underlying.common.exception.ShardingSphereException;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.jndi.JndiObjectFactoryBean;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 因原生的{@link org.apache.shardingsphere.shardingjdbc.spring.boot.SpringBootConfiguration}会造成spring-jpa的相关自动装配不符合条件；
 * 如：
 * JdbcTemplateAutoConfiguration, HibernateJpaConfiguration,
 * DataSourceTransactionManagerAutoConfiguration.DataSourceTransactionManagerConfiguration等；
 * Report如下：Did not match: - @ConditionalOnSingleCandidate (types: javax.sql.DataSource; SearchStrategy: all)
 * did not find a primary bean from beans 'shardingDataSource', 'dataSource'
 * <br>
 * 注：只在设置spring.shardingsphere.enabled=true时才触发自动装配
 *
 * @author : Eric Lu
 * @version :
 * @date : 2020-07-30 15:11
 * @since :
 **/
@Configuration
@ComponentScan("org.apache.shardingsphere.spring.boot.converter")
@EnableConfigurationProperties({
        SpringBootShardingRuleConfigurationProperties.class,
        SpringBootMasterSlaveRuleConfigurationProperties.class,
        SpringBootEncryptRuleConfigurationProperties.class,
        SpringBootPropertiesConfigurationProperties.class,
        SpringBootShadowRuleConfigurationProperties.class,
        GmCommonProperties.class
})
@ConditionalOnProperty(prefix = "spring.shardingsphere", name = "enabled", havingValue = "true")
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@Import({
        DataSourceContextConfig.class,
        JPAContextConfig.class,
        MultipleJdbc4QuerydslContextConfig.class,
        TransactionManagementContextConfig.class,
        AsyncExtContextConfig.class
})
public class ShardingJdbcAutoConfiguration implements EnvironmentAware {

    private final SpringBootShardingRuleConfigurationProperties shardingRule;

    private final SpringBootMasterSlaveRuleConfigurationProperties masterSlaveRule;

    private final SpringBootEncryptRuleConfigurationProperties encryptRule;

    private final SpringBootShadowRuleConfigurationProperties shadowRule;

    private final SpringBootPropertiesConfigurationProperties props;

    private final Map<String, DataSource> dataSourceMap = new LinkedHashMap<>();

    private final String jndiName = "jndi-name";

    public ShardingJdbcAutoConfiguration(SpringBootShardingRuleConfigurationProperties shardingRule,
                                         SpringBootMasterSlaveRuleConfigurationProperties masterSlaveRule,
                                         SpringBootEncryptRuleConfigurationProperties encryptRule,
                                         SpringBootShadowRuleConfigurationProperties shadowRule,
                                         SpringBootPropertiesConfigurationProperties props) {
        this.shardingRule = shardingRule;
        this.masterSlaveRule = masterSlaveRule;
        this.encryptRule = encryptRule;
        this.shadowRule = shadowRule;
        this.props = props;
    }

    Table<String, DbType, DataSource> dataSourceTable = HashBasedTable.create();

    @Bean("multipleDataSourceTable")
    public Table<String, DbType, DataSource> getDataSourceTable(){
        return dataSourceTable;
    }

    /**
     * Get sharding data source bean.
     *
     * @return data source bean
     * @throws SQLException SQL exception
     */
    @Bean
    @Primary
    @Conditional(ShardingRuleCondition.class)
    public DataSource shardingDataSource() throws SQLException {
        return ShardingDataSourceFactory.createDataSource(
                dataSourceMap,
                new ShardingRuleConfigurationYamlSwapper().swap(shardingRule),
                props.getProps()
        );
    }

    /**
     * Get master-slave data source bean.
     *
     * @return data source bean
     * @throws SQLException SQL exception
     */
    @Bean
    @Primary
    @Conditional(MasterSlaveRuleCondition.class)
    public DataSource masterSlaveDataSource() throws SQLException {
        return MasterSlaveDataSourceFactory.createDataSource(
                dataSourceMap,
                new MasterSlaveRuleConfigurationYamlSwapper().swap(masterSlaveRule),
                props.getProps()
        );
    }

    /**
     * Get encrypt data source bean.
     *
     * @return data source bean
     * @throws SQLException SQL exception
     */
    @Bean
    @Primary
    @Conditional(EncryptRuleCondition.class)
    public DataSource encryptDataSource() throws SQLException {
        return EncryptDataSourceFactory.createDataSource(
                dataSourceMap.values().iterator().next(),
                new EncryptRuleConfigurationYamlSwapper().swap(encryptRule),
                props.getProps()
        );
    }

    /**
     * Get shadow data source bean.
     *
     * @return data source bean
     * @throws SQLException SQL exception
     */
    @Bean
    @Primary
    @Conditional(ShadowRuleCondition.class)
    public DataSource shadowDataSource() throws SQLException {
        return ShadowDataSourceFactory.createDataSource(
                dataSourceMap,
                new ShadowRuleConfigurationYamlSwapper().swap(shadowRule),
                props.getProps()
        );
    }

    /**
     * Create sharding transaction type scanner.
     *
     * @return sharding transaction type scanner
     */
    @Bean
    public ShardingTransactionTypeScanner shardingTransactionTypeScanner() {
        return new ShardingTransactionTypeScanner();
    }

    @Override
    public final void setEnvironment(final Environment environment) {
        String prefix = "spring.shardingsphere.datasource.";
        for (String each : getDataSourceNames(environment, prefix)) {
            try {
                dataSourceMap.put(each, getDataSource(environment, prefix, each));
            } catch (final ReflectiveOperationException ex) {
                throw new ShardingSphereException("Can't find datasource type!", ex);
            } catch (final NamingException namingEx) {
                throw new ShardingSphereException("Can't find JNDI datasource!", namingEx);
            }
        }
    }

    private List<String> getDataSourceNames(final Environment environment, final String prefix) {
        StandardEnvironment standardEnv = (StandardEnvironment) environment;
        standardEnv.setIgnoreUnresolvableNestedPlaceholders(true);
        return ValidateUtilExt.isNullOrEmpty(standardEnv.getProperty(prefix + "name"))
                ? new InlineExpressionParser(standardEnv.getProperty(prefix + "names")).splitAndEvaluate()
                : Collections.singletonList(standardEnv.getProperty(prefix + "name"));
    }

    @SuppressWarnings("unchecked")
    private DataSource getDataSource(final Environment environment, final String prefix, final String dataSourceName)
            throws ReflectiveOperationException, NamingException {

        Map<String, Object> dataSourceProps = PropertyUtil.handle(
                environment,
                prefix + dataSourceName.trim(),
                Map.class
        );
        Preconditions.checkState(!dataSourceProps.isEmpty(), "Wrong datasource properties!");
        if (dataSourceProps.containsKey(jndiName)) {
            return getJndiDataSource(dataSourceProps.get(jndiName).toString());
        }
        DataSource result = DataSourceUtil.getDataSource(dataSourceProps.get("type").toString(), dataSourceProps);
        DataSourcePropertiesSetterHolder
                .getDataSourcePropertiesSetterByType(dataSourceProps.get("type").toString())
                .ifPresent(
                        dataSourcePropertiesSetter ->
                                dataSourcePropertiesSetter.propertiesSet(
                                        environment,
                                        prefix,
                                        dataSourceName,
                                        result
                                )
                );
        String driverClassName = (String) dataSourceProps.get("driver-class-name");
        Utils.populateDataSourceTable(dataSourceName, result, driverClassName, dataSourceTable);
        return result;
    }

    private DataSource getJndiDataSource(final String jndiName) throws NamingException {
        JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
        bean.setResourceRef(true);
        bean.setJndiName(jndiName);
        bean.setProxyInterface(DataSource.class);
        bean.afterPropertiesSet();
        return (DataSource) bean.getObject();
    }

}
