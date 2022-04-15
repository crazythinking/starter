# Starter
infrastructure gears based spring boot 2.1.4

---
模块介绍

|模块|模块名|简介|
|----|----|----|
|bustream-spring-boot-starter|消息队列组件|-|
|debezium-spring-boot-starter|CDC中间件|-|
|disruptor-spring-boot-starter|Disruptor+Spring的编程模型组件|-|
|distlock-spring-boot-starter|分布式锁组件|-|
|dynamic-datasource-spring-boot-starter|动态数据库及存储支持组件|-|
|elasticsearch-spring-boot-starter|Elasticsearch中间件|-|
|gm-spring-boot-starter|通用工程的自动化配置组件|-|
|kettle-spring-boot-starter|Kettle中间件|-|
|minio-spring-boot-starter|Minio中间件|-|
|redis-spring-boot-starter|Redis中间件|-|
|rocksdb-spring-boot-starter|Rocksdb中间件|-|
|transflow-spring-boot-starter|进程内可编排的交易流程中间件|-|
|zeebe-spring-boot-starter|微服务间交易流程编排中间件|-|


---
变更记录

|分支|版本|修改内容|分支开立时间|分支合并时间|
|----|----|----|----|----|
|master|1.1.0.RELEASE|1、datasource<br>-  druid<br>-  hikari<br>-  shardingjdbc<br>2、Redis|-|-|
|1.0.0|1.0.0.RELEASE|1、kettle 初始化|-|-|
|1.1.0|1.1.0.RELEASE|1、kettle  <br> -   reop入参bug修改|-|-|
|1.2.0|1.2.0.RELEASE|1、添加分布式锁服务distlock<br>2、增加transflow并发处理|-|-|
|2.0.0|2.0.0.RELEASE|1、oracle方言<br>2、org增强aop<br>3、starter自动注入和适配|-|-|

# 1.2.0版本火车

|包名|版本|说明|
|----|----|----|
|starter-parent|1.1.0-SNAPSHOT|自身包|
|project-parent|3.6.RELEASE|基础包|
|pg|3.6.2.RELEASE|底层包|
|gm|1.5.2.RELEASE|配置包|
|control-parent|1.1.6.RELEASE|流程控制|

# 1.1.0版本火车

|包名|版本|说明|
|----|----|----|
|starter-parent|1.1.0-SNAPSHOT|自身包|
|project-parent|3.6.RELEASE|基础包|
|pg|3.6.1.RELEASE|底层包|
|gm|1.5.1.RELEASE|配置包|
|control-parent|1.1.5.RELEASE|流程控制|

## 注意事项

###redis

* GEO

    Redis 的 GEO 特性将在 Redis 3.2 版本释出， 这个功能可以将用户给定的地理位置信息储存起来， 并对这些信息进行操作。
    
    因此在使用GEO特性时，确认Redis版本

###kettle

- 注意事项

1. 自定义kettle模块的pom需要注意一下配置

```text
<build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <executable>true</executable>
                </configuration>
            </plugin>
</build>
```

## TODO List
1. distlock-spring-boot-starter 分布式锁；由于大部分金融级系统都需要分布式强一致性，因此从性能和一致性两方面考量推荐基于ZK实现；
   但下一步会增加支持基于 Redis 和数据库的分布式锁；
2. ~~dynamic-datasource-spring-boot-starter 调整Async线程池定义方式，与业务用的隔离；另增加事务事件监听器的抽象类，封装模板代码；
    增加存储层异步同步能力；~~
3. ~~debezium-spring-boot-starter 调整Async线程池定义方式，与业务用的隔离；~~
4. ~~transflow-spring-boot-starter 增加Journal多级存储同步能力，默认同步到Elasticsearch；~~
5. debezium-spring-boot-starter 整合sofa-jRaft；增加WAL机制，确保数据不可丢失性； 增加多活选主能力；
6. rocksdb-spring-boot-starter 增加事务机制的支持；