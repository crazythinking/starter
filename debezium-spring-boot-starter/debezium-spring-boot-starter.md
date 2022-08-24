该组件基于Debezium实现CDC嵌入式的功能；
# Debezium嵌入式架构
![](https://cdn.nlark.com/yuque/0/2021/png/956807/1628320140483-12c77b0f-867e-4325-988a-fde55ea98813.png#crop=0&crop=0&crop=1&crop=1&height=321&id=XMAqk&originHeight=428&originWidth=1490&originalType=binary&ratio=1&rotation=0&showTitle=false&size=0&status=done&style=none&title=&width=1118)
这里的Debezium Server 同时也是Application Server，使微服务具备了对上游指定数据库的CDC能力；
# 使用说明
第一步：starter依赖
```xml
<dependency>
  <groupId>net.engining.starter</groupId>
  <artifactId>debezium-spring-boot-starter</artifactId>
  <version>${starter.version}</version>
</dependency>
```
## 快照存储模式
第二步：

- 如果使用本地文件的存储模式，此模式为默认的，必需要存在如下配置项；此模式不支持CDC connector 的多活部署；
```
#存储history.dat,offset.dat文件的顶层目录
pg.debezium.data-path=/home/user1/cdc
```

- 如果使用Raft的存储模式，则除了上面指定顶层目录外，还需额外激活如下profile：`rheakv.server.default`；此模式可以支持CDC connector 的多活部署，同一时刻只有一个leader connector，其他节点为follower，只作为副本，当leader失效时，某个follower节点会自动竞争为leader，另外此模式部署，需要确保每组connector具有奇数个节点；

默认配置如下：
```yaml
pg:
  debezium:
    #开启 JRaft storage
    enabled-j-raft: true
  rheakv:
    server:
      enabled: true
      store-options:
        placement-driver-options:
          fake: true
        store-engine-options:
          # 本机地址，必须配置
          port: 29001
          # raft log存储目录
          raft-data-path: ${pg.debezium.data-path}/debezium-raft/
          storage-type: rocksdb
          rocksdb-options:
            # kv数据存储目录
            db-path: ${pg.debezium.data-path}/debezium-rocksdb/
            # 是否同步刷盘, 默认为 false, 异步刷盘性能更好, 但是在机器掉电时有丢数据风险；
            # 生产环境断电可能性极低，即使出线也只影响offset和history的持久化，重启后可以从上次offset处继续处理，业务数据不会丢失，做好幂等即可;
            sync: false
            # 默认false，因RheaKV本身会基于raft-log+snapshot确保数据一致性，所以为了更好的性能，对于kv数据就不必再做WAL了；
            disable-w-a-l: false
          common-node-options:
            # raft log snapshot 落盘间隔时间，默认为3600s
            snapshot-interval-secs: 3600
            enable-metrics: true
        use-parallel-compress: true
        # 是否只从 leader 节点读取数据, 默认为true, 当然从follower节点读也能保证线性一致读;
        # 但是如果一个 follower 节点在同步数据时落后较多的情况下, 将导致读请求超时;
        # 从而导致rheaKV client failover逻辑启动重新从leader节点上尝试读取, 最终结果就是读请求延时较长, 但大部分情况下leader的压力不会过大;
        # 但在debezium的场景下只有当前leader会进行offset数据的读写，因此设为true更为合理;
        only-leader-read: true
        # 失败重试次数, 默认为2
        failover-retries: 2
        
```
另外每个节点需要相应配置：
```yaml
pg:
  rheakv:
    server:
      store-options:
        #在没有真实PD的情况下, 必须指定
        initial-server-list: 172.0.0.1:29001,172.0.0.2:29001,172.0.0.3:29001
        store-engine-options:
          # 本机地址，必须配置
          ip: 127.0.0.1
          #在没有真实PD的情况下, 必须指定；一般数据量较大时，需要设置分片规则
          region-engine-options-list:
            - region-id: 1
              #key的前缀必须与pg.debezium.named-properties对应的database.server.name保持一致；
              #每个Debezium Engine对应一个region, 如此每个Debezium Engine所对应的JRaft group都是隔离的；
              start-key: ${pg.debezium.named-properties.xxljob-mysql.database.server.name}
              initial-server-list: 172.0.0.1:29001,172.0.0.2:29001,172.0.0.3:29001
            - region-id: 2
              #key的前缀必须与pg.debezium.named-properties对应的database.server.name保持一致；
              #每个Debezium Engine对应一个region, 如此每个Debezium Engine所对应的JRaft group都是隔离的；
              start-key: ${pg.debezium.named-properties.demods0-mysql.database.server.name}
              initial-server-list: 172.0.0.1:29001,172.0.0.2:29001,172.0.0.3:29001
```
注意：
region的配置，建议为每个connector配置一个region，保持隔离性；start-key与connector配置的database.server.name保持一致；
## connector
第三步：配置connector
```
#连接器的Java类名称
pg.debezium.named-properties.xxljob-mysql.connector.class=io.debezium.connector.mysql.MySqlConnector
#持久化偏移量到存储的周期（毫秒）
pg.debezium.named-properties.xxljob-mysql.offset.flush.interval.ms=10000
pg.debezium.named-properties.xxljob-mysql.database.hostname=ubuntu2004.wsl
pg.debezium.named-properties.xxljob-mysql.database.port=3306
pg.debezium.named-properties.xxljob-mysql.database.user=root
pg.debezium.named-properties.xxljob-mysql.database.password=111111
#解决MySql8连接时Public Key Retrieval is not allowed问题
pg.debezium.named-properties.xxljob-mysql.database.allowPublicKeyRetrieval=true
#由于MySQL的binlog是MySQL复制机制的一部分，为了读取binlog，MySqlConnector实例必须加入MySQL服务器组，
#这意味着该服务器ID在构成MySQL服务器组的所有进程中必须是唯一的，并且是1到2的(32-1)次幂之间的任意整数
pg.debezium.named-properties.xxljob-mysql.database.server.id=1001
#逻辑名称;连接器在其生成的每个源记录的topic字段中包含此逻辑名称，使应用程序能够识别这些记录的来源;注意应于Key保持一致
pg.debezium.named-properties.xxljob-mysql.database.server.name=xxljob-mysql
#包含的数据库列表,多个之间用逗号分隔
pg.debezium.named-properties.xxljob-mysql.database.include.list=xxljob
#是否包含数据库schema层面的变更，默认：true；只跟踪DML的场景，false
pg.debezium.named-properties.xxljob-mysql.include.schema.changes=true
#包含的table列表：databaseName.tableName,多个之间用逗号分隔
pg.debezium.named-properties.xxljob-mysql.table.include.list=xxljob.xxl_job_group
#需要包含的列名：databaseName.tableName.columnName,多个之间用逗号分隔
pg.debezium.named-properties.xxljob-mysql.column.include.list=xxljob.xxl_job_group.id
#指定需要忽略的CDC事件：c(insert/create), u(update), d(delete), r(read), t(truncate)
pg.debezium.named-properties.xxljob-mysql.skipped.operations=r,d
#当捕捉到delete事件时用于额外向kafka发送一个墓碑事件，因此当作为嵌入式的场景，可设为false
pg.debezium.named-properties.xxljob-mysql.tombstones.on.delete=false
#根据项目的实际情况选择Snapshot的初始化类型
pg.debezium.named-properties.xxljob-mysql.snapshot.mode=initial
#捕获CDC事件的时间间隔（毫秒）
pg.debezium.named-properties.xxljob-mysql.poll.interval.ms=1000

```
pg.debezium.named-properties.xxljob-mysql：为组件自定义配置项前缀，其后的配置项命名与官方保持一致；
详情参考：[https://debezium.io/documentation/reference/1.9/connectors](https://debezium.io/documentation/reference/1.9/connectors)
## 监控
第四步：打开监控配置
```
management.endpoint.metrics.enabled=true
management.metrics.export.prometheus.enabled=true
```
# 案例代码
下载获取：debezium-spring-boot-starter-{starter.version}-test-sources.jar，详见如下案例：

- JRaftTestCase.java
- JRaftTestServer2.java
- JRaftTestServer3.java
- SimpleTestCase.java
