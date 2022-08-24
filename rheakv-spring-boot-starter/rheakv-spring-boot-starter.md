该组件基于SOFA-RheaKV，主要用于快速实现基于Raft协议的微服务应用，使微服务具备一致性的、分布式的、嵌入的KV存储能力。
# RheaKV架构
![](https://gw.alipayobjects.com/mdn/rms_da499f/afts/img/A*6K1mTq0z-TkAAAAAAAAAAABjARQnAQ#crop=0&crop=0&crop=1&crop=1&height=748&id=neD8q&originHeight=1496&originWidth=1982&originalType=binary&ratio=1&rotation=0&showTitle=false&status=done&style=none&title=&width=991)

- PD: 全局的中心总控节点，负责整个集群的调度，一个 PD server 可以管理多个集群，集群之间基于RegionId隔离；PD server 需要单独部署，当然，很多场景其实并不需要自管理，rheaKV 也支持不启用 PD，可以使用Fake模式，将不具备动态路由表和扩缩容；通常在嵌入式的部署模式下，建议使用Fake模式；
- Server：数据的存储节点，可按[StartKey, EndKey)范围分为不同的Region，每个Region包含的数据，具有一个独立的Leader节点；写操作只作用于Leader节点，读操作可配置为Follow节点；
   - Store: 集群中的一个物理存储节点，一个 store 包含一个或多个 region
   - Region: 最小的 KV 数据单元，可理解为一个数据分区或者分片，每个 region 都有一个左闭右开的区间 [startKey, endKey)
- Client：从PD获取路由表，根据路由表访问Server节点；
# Server端使用说明
第一步：starter依赖
```xml
<dependency>
  <groupId>net.engining.starter</groupId>
  <artifactId>rheakv-spring-boot-starter</artifactId>
  <version>${starter.version}</version>
</dependency>
```
第二步：通过激活profile默认项：`rheakv.server.default`；
默认配置项如下：
```yaml
pg:
  rheakv:
    server:
      enabled: true
      store-options:
        #在没有真实PD的情况下, 必须指定; 默认使用无独立PD的方式
        placement-driver-options:
          fake: true
        store-engine-options:
          # 本机地址及端口，必须配置，默认端口29001
          port: 29001
          # raft log存储目录
          raft-data-path: ./rheakv-raft/
          storage-type: rocksdb
          rocksdb-options:
            # kv数据存储目录
            db-path: ./rheakv-rocksdb/
            # 是否同步刷盘, 默认为 false, 异步刷盘性能更好, 但是在机器掉电时有丢数据风险；
            sync: false
            # 默认false，因RheaKV本身会基于raft-log+snapshot确保数据一致性，所以为了更好的性能，对于kv数据就不必再做WAL了；
            disable-w-a-l: false
          common-node-options:
            # raft log snapshot 落盘间隔时间，默认为3600s
            snapshot-interval-secs: 3600
            enable-metrics: true
        use-parallel-compress: true
        # 是否只从 leader 节点读取数据, 默认为true, 当然从follower节点读也能保证线性一致读;
        # 但是如果一个 follower 节点在同步数据时落后较多的情况下, 将导致读请求超时,
        # 从而导致 rheaKV client failover 逻辑启动重新从 leader 节点上尝试读取, 最终结果就是读请求延时较长, 但大部分情况下leader的压力不会过大;
        only-leader-read: false
        # 失败重试次数, 默认为2
        failover-retries: 2

```
pg.rheakv.server.store-options.store-engine-options.raft-data-path：raft log 的默认存储位置，该目录会自当创建
pg.rheakv.server.store-options.store-engine-options.rocks-d-b-options.db-path：kv数据的默认存储目录，该目录会自当创建
pg.rheakv.server.store-options.only-leader-read：_是否只从 leader 节点读取数据_
第三步：添加该server节点相应的配置
```yaml
pg:
  rheakv:
    server:
      store-options:
        #在没有真实PD的情况下, 必须指定
        initial-server-list: 127.0.0.1:30001,127.0.0.1:30002,127.0.0.1:30003
        store-engine-options:
          # 本机地址，必须配置，默认端口29001
          ip: 127.0.0.1
          port: 30001
          #在没有真实PD的情况下, 必须指定；一般数据量较大时，需要设置分片规则
          region-engine-options-list:
            - region-id: -1
              initial-server-list: 127.0.0.1:30001,127.0.0.1:30002,127.0.0.1:30003
```

- pg.rheakv.server.store-options.initial-server-list：在PD为Fake模式下（默认），必须配置Server节点；
- pg.rheakv.server.store-options.store-engine-options：本节点的IP及端口等信息；
- pg.rheakv.server.store-options.store-engine-options.region-engine-options-list：数据分片的策略，region-id=-1表示全部数据使用同一个Region；
```yaml
pg:
  rheakv:
    server:
      store-options:
        #在没有真实PD的情况下, 必须指定
        initial-server-list: 127.0.0.1:30001,127.0.0.1:30002,127.0.0.1:30003
        store-engine-options:
          # 本机对外暴露地址，必须配置
          ip: 127.0.0.1
          #默认端口29001，通常建议使用默认
          port: 30001
          #在没有真实PD的情况下, 必须指定；一般数据量较大时，需要设置分片规则
          region-engine-options-list:
            - region-id: 1
              #key的前缀必须与pg.debezium.named-properties对应的database.server.name保持一致；
              #每个Debezium Engine对应一个region, 如此每个Debezium Engine所对应的JRaft group都是隔离的；
              start-key: ${pg.debezium.named-properties.xxljob-mysql.database.server.name}
              initial-server-list: 127.0.0.1:30001,127.0.0.1:30002,127.0.0.1:30003
            - region-id: 2
              #key的前缀必须与pg.debezium.named-properties对应的database.server.name保持一致；
              #每个Debezium Engine对应一个region, 如此每个Debezium Engine所对应的JRaft group都是隔离的；
              start-key: ${pg.debezium.named-properties.demods0-mysql.database.server.name}
              initial-server-list: 127.0.0.1:30001,127.0.0.1:30002,127.0.0.1:30003
```
第四步：该组件会默认整合Server端的监控指标，并按Prometheus的格式输出，注意需要打开如下监控配置：
```
management.endpoint.metrics.enabled=true
management.metrics.export.prometheus.enabled=true
```
第五步：使用；详细API参考：[RheaKV 使用指南](https://www.yuque.com/canghaixiao-ukuea/xumqfi/d6e2b989-e652-462c-b032-7b2d98b82248?view=doc_embed)
```java
//服务实例
@Autowired
KvServer kvServer;

public void testProcess() throws Exception {
    //存储层
    RheaKVStore kvStore = kvServer.getRheaKVStore();
    kvStore.bPut("0", writeUtf8("put_example_value"));
    put(kvStore);
    getSequence(kvStore);
}


```
参考：net.engining.starter:rheakv-spring-boot-starter: net.engining.rheakv.autoconfigure.autotest.cases.RheakvTestServer1
# Client端使用说明
第一步：starter依赖
```xml
<dependency>
  <groupId>net.engining.starter</groupId>
  <artifactId>rheakv-spring-boot-starter</artifactId>
  <version>${starter.version}</version>
</dependency>
```
第二步：通过激活profile默认项：`rheakv.client.default`；
默认配置项如下：
```yaml
pg:
  rheakv:
    client:
      enabled: true
      store-options:
        #在没有真实PD的情况下, 必须指定; 默认使用无独立PD的方式
        placement-driver-options:
          fake: true
        # 是否只从 leader 节点读取数据, 默认为true, 当然从follower节点读也能保证线性一致读, 但是如果一个 follower 节点在同步数据时落后较多的情况下
        # 将导致读请求超时, 从而导致 rheaKV 客户端 failover 逻辑启动重新从 leader 节点上尝试读取, 最终结果就是读请求延时较长
        only-leader-read: false
        # 失败重试次数, 默认为2
        failover-retries: 3
```
第三步：添加该clent节点相应的配置
```yaml
pg:
  rheakv:
    client:
      store-options:
        placement-driver-options:
          #由于PD默认使用fake模式，故取决于 RheaKV Server 的 region-engine-options-list配置，需要保持一致
          region-route-table-options-list:
            - region-id: -1
              initial-server-list: 127.0.0.1:30001,127.0.0.1:30002,127.0.0.1:30003
```
pg.rheakv.client.store-options.placement-driver-options.region-route-table-options-list: 由于PD默认使用fake模式，故取决于 RheaKV Server 的 region-engine-options-list配置，需要保持一致;
第四步：使用；详细API参考：[RheaKV 使用指南](https://www.yuque.com/canghaixiao-ukuea/xumqfi/d6e2b989-e652-462c-b032-7b2d98b82248?view=doc_embed)
```java
//Client实例
@Autowired
KvClient kvClient;

public void testProcess() throws Exception {
    RheaKVStore rheaKVStore = kvClient.getRheaKVStore();
    final List<byte[]> keys = Lists.newArrayList();
    for (int i = 0; i < 10; i++) {
        final byte[] bytes = writeUtf8("iterator_demo_" + i);
        keys.add(bytes);
        rheaKVStore.bPut(bytes, bytes);
    }

    final byte[] firstKey = keys.get(0);
    final byte[] lastKey = keys.get(keys.size() - 1);
    final String firstKeyString = readUtf8(firstKey);
    final String lastKeyString = readUtf8(lastKey);

    final RheaIterator<KVEntry> it1 = rheaKVStore.iterator(firstKey, lastKey, 5);
    final RheaIterator<KVEntry> it2 = rheaKVStore.iterator(firstKey, lastKey, 6, false);
    final RheaIterator<KVEntry> it3 = rheaKVStore.iterator(firstKeyString, lastKeyString, 5);
    final RheaIterator<KVEntry> it4 = rheaKVStore.iterator(firstKeyString, lastKeyString, 6, false);

    for (final RheaIterator<KVEntry> it : new RheaIterator[] { it1, it2, it3, it4 }) {
        while (it.hasNext()) {
            final KVEntry kv = it.next();
            LOGGER.info("Sync iterator: key={}, value={}", readUtf8(kv.getKey()), readUtf8(kv.getValue()));
        }
    }
}
```
参考：net.engining.starter:rheakv-spring-boot-starter: net.engining.rheakv.autoconfigure.autotest.cases.RheakvTestClient
# 案例代码
下载获取：rheakv-spring-boot-starter-{starter.version}-test-sources.jar，详见如下案例：
- RheakvTestClient.java
- RheakvTestServer1.java
- RheakvTestServer2.java
- RheakvTestServer3.java



