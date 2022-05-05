package net.engining.datasource.autoconfigure;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.engining.datasource.autoconfigure.support.DataSourceContextConfig;
import net.engining.datasource.autoconfigure.support.JPAContextConfig;
import net.engining.datasource.autoconfigure.support.MultipleJdbc4QuerydslContextConfig;
import net.engining.datasource.autoconfigure.support.TransactionManagementContextConfig;
import net.engining.datasource.autoconfigure.support.Utils;
import net.engining.pg.support.db.DbType;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.infra.config.mode.ModeConfiguration;
import org.apache.shardingsphere.infra.yaml.config.swapper.mode.ModeConfigurationYamlSwapper;
import org.apache.shardingsphere.sharding.support.InlineExpressionParser;
import org.apache.shardingsphere.spring.boot.ShardingSphereAutoConfiguration;
import org.apache.shardingsphere.spring.boot.datasource.DataSourceMapSetter;
import org.apache.shardingsphere.spring.boot.prop.SpringBootPropertiesConfiguration;
import org.apache.shardingsphere.spring.boot.rule.LocalRulesCondition;
import org.apache.shardingsphere.spring.boot.schema.SchemaNameSetter;
import org.apache.shardingsphere.spring.boot.util.PropertyUtil;
import org.apache.shardingsphere.spring.transaction.TransactionTypeScanner;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 因原生的{@link ShardingSphereAutoConfiguration}会造成spring-data相关依赖{@link DataSource}的自动装配不符合条件；
 * 如：
 * JdbcTemplateAutoConfiguration, HibernateJpaConfiguration,
 * DataSourceTransactionManagerAutoConfiguration.DataSourceTransactionManagerConfiguration等；
 * Report如下：Did not match: - @ConditionalOnSingleCandidate (types: javax.sql.DataSource; SearchStrategy: all)
 * did not find a primary bean from beans 'shardingDataSource', 'dataSource'
 * <br>
 * 注：只在设置pg.datasource.shardingsphere.enabled=true时才触发自动装配
 *
 * @author : Eric Lu
 * @version :
 * @date : 2020-07-30 15:11
 * @since :
 **/
@Configuration
@ComponentScan("org.apache.shardingsphere.spring.boot.converter")
@EnableConfigurationProperties(SpringBootPropertiesConfiguration.class)
@ConditionalOnProperty(prefix = "pg.datasource.shardingsphere", name = "enabled", havingValue = "true")
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@Import({
        DataSourceContextConfig.class,
        JPAContextConfig.class,
        MultipleJdbc4QuerydslContextConfig.class,
        TransactionManagementContextConfig.class
})
public class EnhanceShardingSphereAutoConfiguration implements EnvironmentAware {

    private static final String PREFIX = "spring.shardingsphere.datasource.";

    private static final String DATA_SOURCE_NAME = "name";

    private static final String DATA_SOURCE_NAMES = "names";

    private String schemaName;

    @Autowired
    private SpringBootPropertiesConfiguration props;

    private final Map<String, DataSource> dataSourceMap = new LinkedHashMap<>();

    private final Table<String, DbType, DataSource> dataSourceTable = HashBasedTable.create();

    /**
     * Get mode configuration.
     *
     * @return mode configuration
     */
    @Bean
    public ModeConfiguration modeConfiguration() {
        return null == props.getMode() ? null : new ModeConfigurationYamlSwapper().swapToObject(props.getMode());
    }

    /**
     * Get ShardingSphere data source bean.
     *
     * @param rules      rules configuration
     * @param modeConfig mode configuration
     * @return data source bean
     * @throws SQLException SQL exception
     */
    @Bean
    @Primary
    @Conditional(LocalRulesCondition.class)
    @Autowired(required = false)
    public DataSource shardingSphereDataSource(final ObjectProvider<List<RuleConfiguration>> rules,
                                               final ObjectProvider<ModeConfiguration> modeConfig) throws SQLException {
        Collection<RuleConfiguration> ruleConfigs =
                Optional.ofNullable(rules.getIfAvailable()).orElse(Collections.emptyList());
        return ShardingSphereDataSourceFactory.createDataSource(
                schemaName,
                modeConfig.getIfAvailable(),
                dataSourceMap,
                ruleConfigs,
                props.getProps()
        );
    }

    /**
     * Get data source bean from registry center.
     *
     * @param modeConfig mode configuration
     * @return data source bean
     * @throws SQLException SQL exception
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource dataSource(final ModeConfiguration modeConfig) throws SQLException {
        return !dataSourceMap.isEmpty() ?
                ShardingSphereDataSourceFactory.createDataSource(
                        schemaName, modeConfig, dataSourceMap, Collections.emptyList(), props.getProps())
                : ShardingSphereDataSourceFactory.createDataSource(schemaName, modeConfig);
    }

    /**
     * Create transaction type scanner.
     *
     * @return transaction type scanner
     */
    @Bean
    public TransactionTypeScanner transactionTypeScanner() {
        return new TransactionTypeScanner();
    }

    @Override
    public final void setEnvironment(final Environment environment) {
        dataSourceMap.putAll(DataSourceMapSetter.getDataSourceMap(environment));
        schemaName = SchemaNameSetter.getSchemaName(environment);

        for (String dataSourceName : getDataSourceNames(environment)) {
            Map<String, Object> dataSourceProps = PropertyUtil.handle(
                    environment,
                    PREFIX + dataSourceName.trim(),
                    Map.class
            );

            Preconditions.checkState(!dataSourceProps.isEmpty(), "Wrong datasource properties!");
            String driverClassName = (String) dataSourceProps.get("driver-class-name");
            Utils.populateDataSourceTable(
                    dataSourceName,
                    dataSourceMap.get(dataSourceName),
                    driverClassName,
                    dataSourceTable
            );
        }
    }

    @Bean(Utils.SHARDING_DATA_SOURCE_MAP)
    public Map<String, DataSource> getDataSourceMap() {
        return this.dataSourceMap;
    }

    @Bean(Utils.MULTIPLE_DATA_SOURCE_TABLE)
    public Table<String, DbType, DataSource> getDataSourceTable() {
        return this.dataSourceTable;
    }

    private List<String> getDataSourceNames(final Environment environment) {
        StandardEnvironment standardEnv = (StandardEnvironment) environment;
        standardEnv.setIgnoreUnresolvableNestedPlaceholders(true);
        String dataSourceNames = standardEnv.getProperty(PREFIX + DATA_SOURCE_NAME);
        if (ValidateUtilExt.isNullOrEmpty(dataSourceNames)) {
            dataSourceNames = standardEnv.getProperty(PREFIX + DATA_SOURCE_NAMES);
        }
        return new InlineExpressionParser(dataSourceNames).splitAndEvaluate();
    }

}
