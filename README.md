# Starter
infrastructure gears based spring boot 2.1.4

---
变更记录

|分支|版本|修改内容|分支开立时间|分支合并时间|
|----|----|----|----|----|
|master|1.1.0.RELEASE|1、datasource<br>-  druid<br>-  hikari<br>-  shardingjdbc<br>2、Redis|-|-|
|1.0.0|1.0.0.RELEASE|1、kettle 初始化|-|-|
|1.1.0|1.1.0.RELEASE|1、kettle  <br> -   reop入参bug修改|-|-|
|1.2.0|1.2.0-SNAPSHOT|-|-|-|

# 1.2.0版本火车

|包名|版本|说明|
|----|----|----|
|starter-parent|1.1.0-SNAPSHOT|自身包|
|project-parent|3.6.RELEASE|基础包|
|pg|3.6.1.RELEASE|底层包|
|gm|1.5.1.RELEASE|配置包|
|control-parent|1.1.5.RELEASE|流程控制|

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