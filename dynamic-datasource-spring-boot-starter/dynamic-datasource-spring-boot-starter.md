该组件的目的是提供方便的整合各类主流处理关系型或支持JDBC的数据库相关组件以及扩展能力，包括以下功能：

- 支持动态的多数据源管理；
- 基于ShardingSphere的分库分表能力；
- 支持Hikari和Druid两种数据库连接池；
- QueryDSL+JPA、QueryDSL+JDBC、纯JDBC等ORM支持；
- 规范数据库相关操作相关的开发
# 使用说明
第一步：starter依赖
```xml
<dependency>
  <groupId>net.engining.starter</groupId>
  <artifactId>dynamic-datasource-spring-boot-starter</artifactId>
  <version>${starter.version}</version>
</dependency>
```
## 数据库连接池
第二步：选择连接池实现；同一时刻只能激活其中一个；

- 通过激活profile：`dynamic.hikari.enable`，使用Hikari连接池，以更高的性能著称，将自动注入DynamicDataSourceAutoConfiguration；其将注入如下配置：
   - DataSourceContextConfig；
   - JPAContextConfig；
   - MultipleJdbc4QuerydslContextConfig；
   - TransactionManagementContextConfig；
- 通过激活profile：`dynamic.druid.enable`，使用Druid连接池，将增加更为丰富的针对数据库操作的监控和管理功能，包括慢SQL、过滤器等，将自动注入DynamicDruidDataSourceAutoConfiguration；其将注入如下配置：
   - DruidSpringAopConfiguration；
   - DruidStatViewServletConfiguration；
   - DruidWebStatFilterConfiguration；
   - DruidFilterConfiguration；
   - DataSourceContextConfig；
   - JPAContextConfig；
   - MultipleJdbc4QuerydslContextConfig；
   - TransactionManagementContextConfig；
   - 另外注意，当使用Druid时，需要显示的依赖，如下：
```xml
<dependency>
  <groupId>com.alibaba</groupId>
  <artifactId>druid-spring-boot-starter</artifactId>
</dependency>
```
## QueryDSL+JPA
该组件默认情况下使用QueryDSL+JPA实现数据库的各种操作；
第三步：需要包含如下配置；

- 通用配置项：application-db.common.properties
```
#Unfortunately, OSIV (Open Session in View) is enabled by default in Spring Boot, and OSIV is really a bad idea from a performance and scalability perspective.
#该设置用于JPA的懒加载，由于容易造成性能问题，不建议使用，禁止使用@ManyToOne这类的注解；
spring.jpa.open-in-view=false
#是否显示SQL语句
spring.jpa.show-sql=true
#配置SQL生成命名策略，用spring默认的会转为小写；
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
spring.jpa.hibernate.ddl-auto=none
#显示SQL语句时是否format，生产环境不推荐设为true
spring.jpa.properties.hibernate.format_sql=false

```

- 特定数据库配置项，配置文件的命名规范为：application-{连接池组件名称}.{数据库名称}.properties，如使用Hikari连接池，操作Mysql数据库，那么命名应为：`application-hikari.mysql.properties`；同时还需注意`spring.jpa.database`的配置，其默认情况会作用于所有数据源，但如果多个数据源存在异构情况时，非Default数据源由于方言的限制将不能支持JPA操作，此时建议使用QueryDSL+JDBC的方式；
```
#在需要兼容JPA的项目中：通常必须用关系型数据库作为主库，非关系型数据库作为辅库，因此default-cf必须与spring.jpa.database绑定，用于方言的自动装配；
#另外注意JPA是无法支持非关系型数据库的；
spring.jpa.database=mysql
#默认数据源的配置，必须配置
pg.datasource.dynamic.hikari.default-cf.driver-class-name=com.mysql.cj.jdbc.Driver
pg.datasource.dynamic.hikari.default-cf.jdbc-url=jdbc:mysql://ubuntu.wsl:3306/xxljob?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Hongkong&zeroDateTimeBehavior=convertToNull
pg.datasource.dynamic.hikari.default-cf.username=user
pg.datasource.dynamic.hikari.default-cf.password=password

```
第四步：注入Entity以及JpaRepository；其中Entity是必须注入的，应该包含项目中Dict工程定义的实体模型（ERM定义），以及使用到PaaS平台框架中特定功能的实体模型；而JpaRepository不是必须的，通常建议简单逻辑的单表操作可以使用，复杂逻辑或需要限定select子句返回数据的情况下使用QueryDSL语法实现；
```java
@Configuration
@EnableJpaRepositories({
        "net.engining.datasource.autoconfigure.autotest.jpa.support"
})
@EntityScan(basePackages = {
        "net.engining.datasource.autoconfigure.autotest.jpa.support",
        //PaaS平台框架gm模块中的相关实体模型
        "net.engining.gm.entity.model"
})
public class CombineContextConfig {

}
```
@EnableJpaRepositories：指定"net.engining.datasource.autoconfigure.autotest.jpa.support"下的带有@Repository注解的所有类被注入；
@EntityScan：指定"net.engining.datasource.autoconfigure.autotest.jpa.support"和"net.engining.gm.entity.model"下的带有@Entity注解的所有类被注入；
![image.png](https://cdn.nlark.com/yuque/0/2022/png/956807/1658120468824-46bb4a65-f751-4363-bb0f-108a7f467b54.png#clientId=u40c2c451-7598-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=506&id=ufd1b566f&margin=%5Bobject%20Object%5D&name=image.png&originHeight=506&originWidth=800&originalType=binary&ratio=1&rotation=0&showTitle=false&size=57371&status=done&style=none&taskId=u5efb67f2-33b5-4cbd-ae0a-dba7977f7cd&title=&width=800)
参考：net.engining.starter:dynamic-datasource-spring-boot-starter: net.engining.datasource.autoconfigure.autotest.jpa.cases.SimpleTestCase
## QueryDSL+JDBC
第五步：由于QueryDSL+JDBC支持更加灵活的数据库操作，因此对于少数QueryDSL+JPA无法支持的复杂数据库操作，比如batch insert、子查询、复杂join、union、窗口函数等，可以通过QueryDSL+JDBC实现；该组件会自动注入Bean：sqlQueryFactoryMap，其类型为Map<String, SQLQueryFactory>，key为前面配置的DataSource名称，"default-cf"对应为"default"；
```java
//获取所有DataSource对应的SQLQueryFactory
@Autowired
@Qualifier(Utils.SQL_QUERY_FACTORY_MAP)
Map<String, SQLQueryFactory> sqlQueryFactoryMap;

private long extractedSave(List<OperAdtLogDto> operAdtLogList) {
    //获取当前线程绑定的DataSourceKey
    String key = DataSourceContextHolder.getCurrentDataSourceKey();
    //从Map中拿到对应的SQLQueryFactory
    SQLQueryFactory sqlQueryFactory = sqlQueryFactoryMap.get(key);

    QSqlOperAdtLog qSqlOperAdtLog = QSqlOperAdtLog.operAdtLog;
    SQLInsertClause insertClause = sqlQueryFactory.insert(qSqlOperAdtLog);
    //设置insert为一条批量语句，而非多条insert语句，可减少对数据库的IO操作，提高性能
    insertClause.setBatchToBulk(true);

    for (OperAdtLogDto operAdtLog : operAdtLogList){
        insertClause
            .set(qSqlOperAdtLog.id, operAdtLog.getId())
            .set(qSqlOperAdtLog.operTime, getDate(operAdtLog.getOperTime()))
            .set(qSqlOperAdtLog.loginId, operAdtLog.getLoginId())
            .set(qSqlOperAdtLog.jpaVersion, 0)
            .set(qSqlOperAdtLog.requestBody, operAdtLog.getRequestBody())
            .set(qSqlOperAdtLog.requestUri, operAdtLog.getRequestUri())
            ;
        insertClause.addBatch();
    }

    //将参数值带入sql语句
    insertClause.setUseLiterals(true);
    for (SQLBindings sqlBindings : insertClause.getSQL()){
        LOGGER.debug(sqlBindings.getSQL());
    }

    return insertClause.execute();
}
```
参考：net.engining.starter:dynamic-datasource-spring-boot-starter: net.engining.datasource.autoconfigure.autotest.qsql.cases.SimpleTestCase
## 纯JDBC
第六步：当需要实现一些相较于上面两种方式更灵活以及基于数据库特性的数据库操作时，可以使用纯JDBC模式，比如Hint、调用存储过程、DDL等；需要显示依赖：
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jdbc</artifactId>
</dependency>
```

- 可以支持Spring JDBC Repository，但需要激活额外的扫描器，@EnableJdbcRepositories：指定"net.engining.datasource.autoconfigure.autotest.jdbc.support"下的带有@Repository注解的所有类被注入；

![image.png](https://cdn.nlark.com/yuque/0/2022/png/956807/1658135106566-54d964d3-5bd8-4940-8a68-3503b5bf0a2f.png#clientId=u18f1c5cf-14da-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=741&id=u7338c9c2&margin=%5Bobject%20Object%5D&name=image.png&originHeight=741&originWidth=963&originalType=binary&ratio=1&rotation=0&showTitle=false&size=91189&status=done&style=none&taskId=ub648174c-ef2f-4a3c-9c10-0b2f84051a2&title=&width=963)

- 另外还需要根据数据库操作实体注册绑定相应的RowMapper，RowMapper绑定的通常是用户自定义的Dto，如上图所示；
```java
public class OperAdtLogExtDto extends OperAdtLogDto {

    private String hashedRequestBody;

    public String getHashedRequestBody() {
        return hashedRequestBody;
    }

    public void setHashedRequestBody(String hashedRequestBody) {
        this.hashedRequestBody = hashedRequestBody;
    }
    
    //JDBC模式需要将原生的ResultSet显示的转换到Dto
    public static class OperAdtLogExtDtoRowMapper implements RowMapper<OperAdtLogExtDto> {

        @Override
        public OperAdtLogExtDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            OperAdtLogExtDto operAdtLogExtDto = new OperAdtLogExtDto();
            operAdtLogExtDto.setId(rs.getInt(OperAdtLog.P_ID));
            operAdtLogExtDto.setLoginId(rs.getString(OperAdtLog.P_LOGIN_ID));
            operAdtLogExtDto.setRequestUri(rs.getString(OperAdtLog.P_REQUEST_URI));
            operAdtLogExtDto.setOperTime(rs.getDate(OperAdtLog.P_OPER_TIME));
            operAdtLogExtDto.setRequestBody(rs.getString(OperAdtLog.P_REQUEST_BODY));
            operAdtLogExtDto.setHashedRequestBody(
                    String.valueOf(
                            HashUtil.tianlHash(
                                    Joiner.on("|").join(
                                            rs.getString(OperAdtLog.P_REQUEST_URI),
                                            rs.getString(OperAdtLog.P_REQUEST_BODY))
                            )
                    )
            );
            return operAdtLogExtDto;
        }
    }
}
```

- Repository的定义如下：
```java
@Repository
public interface OperAdtLogJdbcRepository extends PagingAndSortingRepository<OperAdtLog, Integer> {

    /**
     * 注意这里返回的并不是原始的OperAdtLog，而是额外定义的DTO，需要有对应的RowMapper
     */
    @Query(
            value = "select * from OPER_ADT_LOG o where o.LOGIN_ID = :loginId",
            rowMapperClass = OperAdtLogExtDto.OperAdtLogExtDtoRowMapper.class
    )
    List<OperAdtLogExtDto> findByLoginId(@Param("loginId") String loginId);

    /**
     * 基于Named-SQL配置文件，默认在classpath下”META-INF/jdbc-named-queries.properties“
     */
    @Query(
            name = "OperAdtLog.fetchWhereUri",
            rowMapperClass = OperAdtLogExtDto.OperAdtLogExtDtoRowMapper.class
    )
    List<OperAdtLogExtDto> fetchWhereUri(@Param("uri") String uri);

}
```

   - 其中Named-SQL如下：

![image.png](https://cdn.nlark.com/yuque/0/2022/png/956807/1658136637278-5d76f9c0-f285-4c06-a7e3-e66f435dac1b.png#clientId=u18f1c5cf-14da-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=26&id=u937ca466&margin=%5Bobject%20Object%5D&name=image.png&originHeight=26&originWidth=706&originalType=binary&ratio=1&rotation=0&showTitle=false&size=6616&status=done&style=none&taskId=u4b08ba20-d6b3-4c8d-a04a-1b1a7b9b8ac&title=&width=706)
参考：net.engining.starter:dynamic-datasource-spring-boot-starter: net.engining.datasource.autoconfigure.autotest.jdbc.cases.SimpleTestCase
## 多数据源
第七步：当需要在应用中同时连接多个数据源，并需要根据业务处理逻辑进行动态切换时，可以配置多个不同的数据源；以使用Hikari连接池为例：
```
#只在使用多数据源时配置，其中one作为该数据源的key，可以使用任意String作为key
pg.datasource.dynamic.hikari.cf-map.one.driver-class-name=com.mysql.cj.jdbc.Driver
pg.datasource.dynamic.hikari.cf-map.one.jdbc-url=jdbc:mysql://ubuntu.wsl:3306/oauth2?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Hongkong&zeroDateTimeBehavior=convertToNull
pg.datasource.dynamic.hikari.cf-map.one.username=user
pg.datasource.dynamic.hikari.cf-map.one.password=password
```

- 使用@SpecifiedDataSource指定数据源进行切换，如下切换到"one"指定的DataSource，注意该注解只作用与方法，方法退出后，数据源将恢复到默认的；
```java
@SpecifiedDataSource("one")
public List<OperAdtLogExtDto> fetchByLogin4Ck(String login) {
    return operAdtLogJdbcRepository.findByLoginId(login);
}
```
参考：net.engining.starter:dynamic-datasource-spring-boot-starter:

- net.engining.datasource.autoconfigure.autotest.jdbc.cases.support.LogRepositoriesServiceImpl
- net.engining.datasource.autoconfigure.autotest.qsql.cases.support.LogRepositoriesServiceImpl
- net.engining.datasource.autoconfigure.autotest.jpa.support.DbService
## 分库分表
第八步：基于ShardingSphere组件扩展，需要显示的激活profile：`shardingsphere.enable`；

- 通用配置项：application-db.common.properties，同上；
- sharding通用配置项：application-db.sharding.common.properties；如下案例包含了运行模式以及分片规则策略的配置：
```
#持久化用户配置以及元数据信息是分布式治理最主要的功能之一，也是支持 DistSQL 的基本能力。
# 运行模式类型。可选配置：Memory、Standalone、Cluster
# Standalone模式
spring.shardingsphere.mode.type=Standalone
# 持久化仓库类型
spring.shardingsphere.mode.repository.type=File
# 持久化仓库所需属性
spring.shardingsphere.mode.repository.props.path=/home/user1/shardingsphere
# 是否使用本地配置覆盖持久化配置
#spring.shardingsphere.mode.overwrite=

# Cluster模式, 用于集群测试
#spring.shardingsphere.mode.type=Cluster
# 持久化仓库类型
#spring.shardingsphere.mode.repository.type=
# 注册中心命名空间
#spring.shardingsphere.mode.repository.props.namespace=
# 注册中心连接地址
#spring.shardingsphere.mode.repository.props.server-lists=
# 持久化仓库所需属性
#spring.shardingsphere.mode.repository.props.<key>=
# 是否使用本地配置覆盖持久化配置
#spring.shardingsphere.mode.overwrite=

#定义数据源key
spring.shardingsphere.datasource.names=ds0,ds1
spring.shardingsphere.props.sql-show=true

# sharding策略绑定的表
spring.shardingsphere.rules.sharding.binding-tables=t_order,t_order_item

# 默认分库策略: 未指定策略时，只要被绑定的表中含有SNOW_FLAKE_ID列，则使用该策略
spring.shardingsphere.rules.sharding.default-database-strategy.standard.sharding-column=user_id
spring.shardingsphere.rules.sharding.default-database-strategy.standard.sharding-algorithm-name=default-db
# 默认分库策略
#spring.shardingsphere.rules.sharding.sharding-algorithms.default-db.type=MOD
#spring.shardingsphere.rules.sharding.sharding-algorithms.default-db.props.sharding-count=2
# 默认分库策略，与上方策略等价
spring.shardingsphere.rules.sharding.sharding-algorithms.default-db.type=INLINE
spring.shardingsphere.rules.sharding.sharding-algorithms.default-db.props.algorithm-expression=ds$->{user_id % 2}
# 指定表的分片模式，此例只分库，不分表
spring.shardingsphere.rules.sharding.tables.t_order.actual-data-nodes=ds$->{0..1}.t_order
spring.shardingsphere.rules.sharding.tables.t_order_item.actual-data-nodes=ds$->{0..1}.t_order_item

# 指定表的分库策略
#spring.shardingsphere.rules.sharding.tables.t_order.database-strategy.standard.sharding-column=user_id
#spring.shardingsphere.rules.sharding.tables.t_order.database-strategy.standard.sharding-algorithm-name=db-tOrder
# 指定表的分库策略规则
#spring.shardingsphere.rules.sharding.sharding-algorithms.db-tOrder.type=INLINE
#spring.shardingsphere.rules.sharding.sharding-algorithms.db-tOrder.props.algorithm-expression=ds$->{user_id % 2}
#spring.shardingsphere.rules.sharding.sharding-algorithms.db-tOrder.type=MOD
#spring.shardingsphere.rules.sharding.sharding-algorithms.db-tOrder.props.sharding-count=2
# 指定表的分表策略
#spring.shardingsphere.rules.sharding.tables.t_order.table-strategy.standard.sharding-column=user_id
#spring.shardingsphere.rules.sharding.tables.t_order.table-strategy.standard.sharding-algorithm-name=tb-tOrder
# 指定表的分表策略规则
#spring.shardingsphere.rules.sharding.sharding-algorithms.tb-tOrder.type=INLINE
#spring.shardingsphere.rules.sharding.sharding-algorithms.tb-tOrder.props.algorithm-expression=tOrder$->{user_id % 2}
#spring.shardingsphere.rules.sharding.sharding-algorithms.tb-tOrder.type=MOD
#spring.shardingsphere.rules.sharding.sharding-algorithms.tb-tOrder.props.sharding-count=2

#直接限定PG_ID_TEST_Ent1在ds1,其场景是表PG_ID_TEST_Ent1只在ds1所代表的数据库
#spring.shardingsphere.rules.sharding.tables.PG_ID_TEST_Ent1.actual-data-nodes=ds1.PG_ID_TEST_Ent1

```

- 注意运行模式的选择取决于是否需要依赖ShardingSphere持久化元数据，以及如何持久化，关于ShardingSphere使用的元数据，参考：[元数据在 ShardingSphere 中加载的过程](https://shardingsphere.apache.org/blog/cn/material/oct_12_1_shardingspheres_metadata_loading_process/#:~:text=%E8%80%8C%20ShardingSphere,%E4%B8%AD%E7%9A%84%E6%A0%B8%E5%BF%83%E5%8A%9F%E8%83%BD%E5%A6%82%E6%95%B0%E6%8D%AE%E5%88%86%E7%89%87%E3%80%81%E5%8A%A0%E8%A7%A3%E5%AF%86%E7%AD%89%E9%83%BD%E6%98%AF%E9%9C%80%E8%A6%81%E5%9F%BA%E4%BA%8E%E6%95%B0%E6%8D%AE%E5%BA%93%E7%9A%84%E5%85%83%E6%95%B0%E6%8D%AE%E7%94%9F%E6%88%90%E8%B7%AF%E7%94%B1%E6%88%96%E8%80%85%E5%8A%A0%E5%AF%86%E8%A7%A3%E5%AF%86%E7%9A%84%E5%88%97%E5%AE%9E%E7%8E%B0%EF%BC%8C%E7%94%B1%E6%AD%A4%E5%8F%AF%E8%A7%81%E5%85%83%E6%95%B0%E6%8D%AE%E6%98%AF%20ShardingSphere%20%E7%B3%BB%E7%BB%9F%E8%BF%90%E8%A1%8C%E7%9A%84%E6%A0%B8%E5%BF%83%EF%BC%8C%E5%90%8C%E6%A0%B7%E4%B9%9F%E6%98%AF%E6%AF%8F%E4%B8%80%E4%B8%AA%E6%95%B0%E6%8D%AE%E5%AD%98%E5%82%A8%E7%9B%B8%E5%85%B3%E4%B8%AD%E9%97%B4%E4%BB%B6%E6%88%96%E8%80%85%E7%BB%84%E4%BB%B6%E7%9A%84%E6%A0%B8%E5%BF%83%E6%95%B0%E6%8D%AE%E3%80%82)，[ShardingSphere元数据中心设计](https://zhuanlan.zhihu.com/p/115466664)；
   - 元数据是表示数据的数据。从数据库角度而言，则概括为数据库的任何数据都是元数据，因此如列名、数据库名、用户名、表名等以及数据自定义库表存储的关于数据库对象的信息都是元数据。而 ShardingSphere 中的核心功能如数据分片、加解密等都是需要基于数据库的元数据生成路由或者加密解密的列实现，由此可见元数据是 ShardingSphere 系统运行的核心，同样也是每一个数据存储相关中间件或者组件的核心数据。有了元数据的注入，相当于整个系统有了神经中枢，可以结合元数据完成对于库、表、列的个性化操作，如数据分片、数据加密、SQL 改写等。
   - **内存模式：**初始化配置或执行 SQL 等造成的元数据结果变更的操作，仅在当前进程中生效。 适用于集成测试的环境启动，方便开发人员在整合功能测试中集成 Apache ShardingSphere 而无需清理运行痕迹。
   - **单机模式：**能够将数据源和规则等元数据信息持久化，但无法将元数据同步至多个 Apache ShardingSphere 实例，无法在集群环境中相互感知。 通过某一实例更新元数据之后，会导致其他实例由于获取不到最新的元数据而产生不一致的错误。 适用于工程师在本地搭建 Apache ShardingSphere 环境。
   - **集群模式：**提供了多个 Apache ShardingSphere 实例之间的元数据共享和分布式场景下状态协调的能力。 在真实部署上线的生产环境，必须使用集群模式。它能够提供计算能力水平扩展和高可用等分布式系统必备的能力。 集群环境需要通过独立部署的注册中心来存储元数据和协调节点状态。
   - 三种运行模式的对比如下，用户可以按照自己的需求进行选择；
|  | **内存模式** | **单机模式** | **集群模式** |
| --- | --- | --- | --- |
| 默认 | 是 | 否 | 否 |
| 持久化方式 | 无 | 本地文件（默认） | 注册中心 |
| 实现方式 | 无 | 1.File  | 1.Zookeeper 2. Etcd |
| 是否需要部署三方组件 | 否 | 否 | 是 |
| 是否支持 DistSQL | 是 | 是 | 是 |
| 分布式治理 | 否 | 否 | 是 |
| 适用场景 | 集成测试/快速验证 | 本地开发/功能联调 | 生产环境/混合部署 |
| 元数据加载 | 每次启动通过数据源加载 | 首次通过数据源加载后，每次启动通过指定路径生成的文件加载 | 首次只需一个节点通过数据源加载后，其他节点从注册中心加载 |

- 配置详细的数据源信息，注意key的定义要与上面的配置（`spring.shardingsphere.datasource.names`）保持一致；
```
#此处配置决定了SQL方言
spring.jpa.database=mysql

spring.shardingsphere.datasource.ds0.type=com.zaxxer.hikari.HikariDataSource
spring.shardingsphere.datasource.ds0.driver-class-name=com.mysql.cj.jdbc.Driver
spring.shardingsphere.datasource.ds0.jdbc-url=jdbc:mysql://ubuntu2004.wsl:3306/demo_ds_0?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Hongkong&zeroDateTimeBehavior=convertToNull
spring.shardingsphere.datasource.ds0.username=user
spring.shardingsphere.datasource.ds0.password=password
# 数据库连接池的其它属性，比如线程池大小、最大等待时间等
#spring.shardingsphere.datasource.ds0.<xxx>=

spring.shardingsphere.datasource.ds1.type=com.zaxxer.hikari.HikariDataSource
spring.shardingsphere.datasource.ds1.driver-class-name=com.mysql.cj.jdbc.Driver
spring.shardingsphere.datasource.ds1.jdbc-url=jdbc:mysql://ubuntu2004.wsl:3306/demo_ds_1?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Hongkong&zeroDateTimeBehavior=convertToNull
spring.shardingsphere.datasource.ds1.username=user
spring.shardingsphere.datasource.ds1.password=password
```

- 使用的连接池实现由`spring.shardingsphere.datasource.<ds-key>.type`指定；
- 需要注意的是ShardingSphere本身只用作分库分表的分布式组件，不支持异构数据源的操作；

参考：net.engining.starter:dynamic-datasource-spring-boot-starter:net.engining.datasource.autoconfigure.autotest.sharding.cases.SimpleShardingTestCase
