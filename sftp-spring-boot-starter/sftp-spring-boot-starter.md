该组件基于Spring-Integration实现SFTP的统一处理；
# 使用说明
第一步：starter依赖
```xml
<dependency>
  <groupId>net.engining.starter</groupId>
  <artifactId>sftp-spring-boot-starter</artifactId>
  <version>${starter.version}</version>
</dependency>
```
第二步：配置多源SFTP
```yaml
pg:
  sftp:
    muti:
      default-sftp-properties:
        host: ubuntu2004.wsl
        port: 2222
        user: luxue
        password: luxue1981
        default-remote-directory: /home/luxue/sftp-test
        default-local-directory: ./sftpinput/
      named-sftp-properties:
        sftp-test1:
          host: ubuntu2004.wsl
          port: 2222
          user: luxue
          password: luxue1981
          default-remote-directory: /home/luxue/sftp-test1
          default-local-directory: ./sftpinput1/
        sftp-test2:
          host: ubuntu2004.wsl
          port: 2222
          user: luxue
          password: luxue1981
          default-remote-directory: /home/luxue/sftp-test2

```
以上pg.sftp.muti.default-sftp-properties相关的配置项必须具备，pg.sftp.muti.named-sftp-properties根据需要可选；

- pg.sftp.muti.default-sftp-properties：默认连接的SFTP相关属性；
- pg.sftp.muti.named-sftp-properties：其他命名为特定key的SFTP相关属性；
- default-remote-directory或default-local-directory：均为源服务器或目标服务器操作的顶层目录，根据实际情况需要具备读写权限；
- 使用SftpRemoteFileTemplate

第三步：开启shell执行能力，可控粒度到指定SFTP服务器，该步骤可选；
```yaml
pg:
  sftp:
    muti:
      default-sftp-properties:
        exec-enabled: true
      named-sftp-properties:
        sftp-test1:
          exec-enabled: false
        sftp-test2:
          exec-enabled: true

```

- exec-enabled：开启SFTP服务器对应的shell执行能力；
- 组件会根据exec-enabled开关，初始化相应的SSH连接，根据指定的key进行隔离；根据指定key获取ssh连接，执行shell指令；
```java
//组件会根据exec-enabled开关，初始化相应的SSH连接，根据指定的key进行隔离
if (ValidateUtilExt.isNotNullOrEmpty(JschSessionPool.INSTANCE.get(DEFAULT))) {
    String res = JschUtil.exec(
        JschSessionPool.INSTANCE.get(DEFAULT),
        "ls -l /home/luxue/sftp-test | grep 1.txt",
        CharsetUtil.CHARSET_UTF_8,
        System.err
    );
    LOGGER.info("exec res:{}", res);
}
```
第四步：开启SFTP源到目标的自动同步器，可控粒度到指定SFTP服务器，该步骤可选；另外注意，当开启同步时，需要考虑SFTP源目录的文件量和目录层数，或者在源目录具备删除权限，可配置为完成同步的文件自动删除；不能具备删除权限的情况下，建议经验值：待同步源目录的文件总数不超过1000，目录层数不超过3层；
```yaml
pg:
  sftp:
    muti:
      default-sftp-properties:
        #当同步能力打开时，需要指定对应的Disruptor，否则会出现事件无发分发的异常，同时事件将丢失
        sync-enabled: true
        sync-file-name-regex: ".*\\.txt$"
      named-sftp-properties:
        sftp-test1:
          sync-enabled: true
          sync-file-name-regex: ".*\\.txt$"
        sftp-test2:
          sync-enabled: false
          sync-file-name-regex: ".*\\.txt$"
    synchronizer:
      enabled: true
```

- synchronizer.enabled：总体的自动同步开关；
- sync-enabled：指定SFTP的同步开关；
- sync-file-name-regex：指定SFTP的同步文件名的Pattern，支持正则表达式；
- 当同步器打开时，如果是针对非默认的SFTP源，则需要自定义添加相对应的IntegrationFlow；注意：常量`SFTP_TEST_1`需要与配置中的key保持一致，包括后续Disruptor配置的key都要一致；
```java

public static final String SFTP_TEST_1 = "sftp-test1";

/**
 * sftp-test1连接的同步器，需要打开同步器的开关；
 */
@Bean
public IntegrationFlow sftpTest1IntegrationFlow(MutiSftpProperties mutiSftpProperties,
                                                DelegatingSessionFactory<ChannelSftp.LsEntry> delegatingSessionFactory
){
        return SftpConfigUtils.buildIntegrationFlow(
                SFTP_TEST_1,
                mutiSftpProperties.getNamedSftpProperties().get(SFTP_TEST_1),
                delegatingSessionFactory.getFactoryLocator().getSessionFactory(SFTP_TEST_1),
                applicationContext
        );
}
```

- 当同步器打开时，必须指定对应的Disruptor，消费文件的到达事件（只关注文件的增删改事件），并且配置相应的Disruptor配置；
```yaml
pg:
  disruptor:
    grouped-disruptor:
      #注意disruptor的groupKey与sftp配置的key保持一致
      sftp-test1:
        batch-size: 1
        ring-buffer-size: 2048
        #大部分情况文件的到达速率都不会频繁，为减少cpu的空转消耗，建议用此策略
        wait-strategy: blocking_wait
```
添加对应的Disruptor处理类，如下：
```java
/**
 * 注意disruptor的beanName,groupKey与sftp配置的key应始终保持一致
 */
@Bean(SFTP_TEST_1)
public SftpTest1Disruptor sampleFileDisruptor(){
    return new SftpTest1Disruptor(
        applicationContext,
        properties
    );
}

//Disruptor
public class SftpTest1Disruptor extends AbstractBizDataEventDisruptorEngine<File> {

    private DisruptorBizDataEventFactory<File> eventFactory;

    private final List<EventHandler<DisruptorBizDataEvent<File>>> eventHandlers = Lists.newArrayList();

    /**
     * 针对处理SFTP同步器产生的File消息，消息的BizData类型为{@link File}的Disruptor
     *
     * @param applicationContext Spring application context
     */
    public SftpTest1Disruptor(ApplicationContext applicationContext, DisruptorProperties properties) {
        super(applicationContext, SFTP_TEST_1, ExecutionMode.SerialChain);
        initProperties(properties);
        this.eventFactory = new DisruptorBizDataEventFactory<>();

        //setup event handlers
        eventHandlers.add(new SftpTest1FileDisruptorHandler01(SFTP_TEST_1, this.getBatchSize()));
    }

    @Override
    public List<? extends EventHandler<DisruptorBizDataEvent<File>>> getEventHandlers() {
        return eventHandlers;
    }

    @Override
    public DisruptorBizDataEventFactory<File> getEventFactory() {
        return eventFactory;
    }
}

//DisruptorHandler
public class SftpTest1FileDisruptorHandler01
        extends AbstractSerialChainGroupedEventHandler<DisruptorBizDataEvent<File>>{
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(SftpTest1FileDisruptorHandler01.class);

    private static final int ORDER = 1;

    public SftpTest1FileDisruptorHandler01(String groupName, int batchSize) {
        super(groupName, batchSize);
        super.order = ORDER;
    }

    @Override
    protected void doHandlerInternal(DisruptorBizDataEvent<File> event) throws Exception {
        LOGGER.warn(
                "disruptor event ({})",
                event.toString()+ StrUtil.COMMA + " bizData :" +event.getBizData().toString()
        );
        Thread.sleep(1000);
    }

    @Override
    protected void doHandlerInternal(List<DisruptorBizDataEvent<File>> eventBuffer) throws Exception {
        throw new UnsupportedOperationException("not support batch processing");
    }

    @Override
    public boolean isEnabled(DisruptorBizDataEvent<File> event) {
        return true;
    }
}
```
# 案例代码
下载获取：sftp-spring-boot-starter-{starter.version}-test-sources.jar，详见如下案例：

- SimpleTestCase.java
