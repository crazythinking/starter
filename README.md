# Starter
infrastructure gears based spring boot 2.1.4

---
模块介绍

|模块|模块名|简介|
|----|----|----|
|bustream-spring-boot-starter|消息队列组件|-|
|distlock-spring-boot-starter|分布式锁组件|-|
|dynamic-datasource-spring-boot-starter|动态数据库组件|-|
|kettle-spring-boot-starter|kettle中间件|-|
|redis-spring-boot-starter|redis中间件|-|
|transflow-spring-boot-starter|可编排的交易流程中间件|-|
|disruptor-spring-boot-starter|整合Disruptor与Spring的编程模型组件|-|
|debezium-spring-boot-starter|CDC组件|-|



---
变更记录

|分支|版本|修改内容|分支开立时间|分支合并时间|
|----|----|----|----|----|
|master|1.1.0.RELEASE|1、datasource<br>-  druid<br>-  hikari<br>-  shardingjdbc<br>2、Redis|-|-|
|1.0.0|1.0.0.RELEASE|1、kettle 初始化|-|-|
|1.1.0|1.1.0.RELEASE|1、kettle  <br> -   reop入参bug修改|-|-|
|1.2.0|1.2.0.RELEASE|1、添加分布式锁服务distlock<br>2、增加transflow并发处理|-|-|
|1.3.0|1.3.0.RELEASE|1、调整队列配置<br>2、优化默认配置和换行符<br>3、修改测试案例<br>4、底层版本升级<br>5、zeebe组件添加|-|2021-06-10|
|1.4.0|1.4.0.RELEASE|1、增加cdc组件|-|2021-09-22|
|1.4.1|1.4.1.RELEASE|1、CDC排除op=r优化|-|2021-09-22|
|1.4.2|1.4.2-SNAPSHOT|1、字符串判断优化|2021-11-11|-|

# 1.4.2版本火车

|包名|版本|说明|
|----|----|----|
|starter-parent|1.4.2-SNAPSHOT|自身包|
|project-parent|3.6.RELEASE|基础包|
|pg|3.6.5.RELEASE|底层包|
|gm|1.5.3.RELEASE|配置包|
|control-parent|1.1.7.RELEASE|流程控制|

# 1.4.1版本火车

|包名|版本|说明|
|----|----|----|
|starter-parent|1.4.1.RELEASE|自身包|
|project-parent|3.6.RELEASE|基础包|
|pg|3.6.5.RELEASE|底层包|
|gm|1.5.3.RELEASE|配置包|
|control-parent|1.1.7.RELEASE|流程控制|

# 1.4.0版本火车

|包名|版本|说明|
|----|----|----|
|starter-parent|1.4.0.RELEASE|自身包|
|project-parent|3.6.RELEASE|基础包|
|pg|3.6.4.RELEASE|底层包|
|gm|1.5.3.RELEASE|配置包|
|control-parent|1.1.7.RELEASE|流程控制|

# 1.3.0版本火车

|包名|版本|说明|
|----|----|----|
|starter-parent|1.3.0.RELEASE|自身包|
|project-parent|3.6.RELEASE|基础包|
|pg|3.6.3.RELEASE|底层包|
|gm|1.5.3.RELEASE|配置包|
|control-parent|1.1.7.RELEASE|流程控制|

# 1.2.0版本火车

|包名|版本|说明|
|----|----|----|
|starter-parent|1.1.0.RELEASE|自身包|
|project-parent|3.6.RELEASE|基础包|
|pg|3.6.2.RELEASE|底层包|
|gm|1.5.2.RELEASE|配置包|
|control-parent|1.1.6.RELEASE|流程控制|

# 1.1.0版本火车

|包名|版本|说明|
|----|----|----|
|starter-parent|1.1.0.RELEASE|自身包|
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
1. disruptor-spring-boot-starter 目前只整合了EventHandler，因此对于每个独立的Event，其Handler都是单线程模型；下一步需要整合WorkHandler，支持多线程模型；
2. distlock-spring-boot-starter 分布式锁；由于大部分金融级系统都需要分布式强一致性，因此从性能和一致性两方面考量推荐基于ZK实现；
   但下一步会增加支持基于 Redis 和数据库的分布式锁；