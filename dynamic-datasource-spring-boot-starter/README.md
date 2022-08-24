## 功能使用

需要明确激活如下profile之一开启指定的数据源实现：
1. dynamic.hikari.enable
2. dynamic.druid.enable
3. shardingsphere.enable

可使用actuator端点：post dynamicDataSource/add 动态添加数据源  
注意：  
1. 每次只能添加一个数据源。  
2. 需要在配置中心添加相应数据源配置，其对应的key即为端点可接受的参数。
例如，在配置中心添加如下配置：
```text
pg.datasource.dynamic.hikari.cf-map.three.driver-class-name=org.h2.Driver
pg.datasource.dynamic.hikari.cf-map.three.jdbc-url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE
pg.datasource.dynamic.hikari.cf-map.three.username=sa
pg.datasource.dynamic.hikari.cf-map.three.password=
```
则可使用如下指令添加数据源：
```text
curl -X POST http://localhost:8099/mm/actuator/dynamicDataSource/add -d '{"key":"three"}'
```


###Hikari动态多数据源  

####激活  
```text
spring.profiles.active=dynamic.hikari.enable
```

###druid动态多数据源

####激活
```text
spring.profiles.active=dynamic.druid.enable
```

###shardingsphere多数据源  
注意：shardingsphere未支持动态添加数据源

####激活
```text
spring.profiles.active=shardingsphere.enable
```