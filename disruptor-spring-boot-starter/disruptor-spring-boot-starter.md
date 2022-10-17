该组件主要目的是封装简化Disruptor框架的开发，统一 Producer-Consumer 的开发模型；简化支持4种EventHandler执行模式：并行执行、顺序执行、菱形顺序执行、链式依赖执行；<br />其中DisruptorEngine(`net.engining.pg.disruptor.AbstractBizDataEventDisruptorEngine`)作为主控，由构造参数决定了后续EventHandlers的执行模型；而每个EventHandler消费Producer产生的Event，需要注意Event在内存中是同一个对象实例，因此任何一个前序EventHandler的逻辑如果改变了Event，则必然影响后续EventHandler获取的Event；此时在不同的执行模型中对EventHandler的影响也是不同的，尤其是并发相关的执行模型，**需要注意Event对象的线程安全问题**；
<a name="XSXYy"></a>
# 使用说明
**第一步：starter依赖**
```xml
<dependency>
  <groupId>net.engining.starter</groupId>
  <artifactId>disruptor-spring-boot-starter</artifactId>
  <version>${starter.version}</version>
</dependency>
```
<a name="YgiXA"></a>
## 并行执行模式
<a name="WEjeE"></a>
### 第二步：选择执行模式
![](https://cdn.nlark.com/yuque/0/2022/jpeg/956807/1665737930929-dfc5682a-2e32-43ac-8507-fdc1c987e312.jpeg)<br />为了便于理解，图中为每个EventHandler定义了ListIndex和Order，ListIndex决定了分组顺序，按自然顺序排序，Order决定了在组中的执行顺序，按逆自然顺序排序；但对于并行执行模式，其实并不需要ListIndex和Order，因此都为1；<br />**例如并行模式，代码如下：**
```java
@Component(Event1ParallelDisruptor.GROUP_NAME)//[2]
public class Event1ParallelDisruptor extends AbstractBizDataEventDisruptorEngine<String> {//[1]
    
    public final static String GROUP_NAME="Event1-Group";//[2]

    private final List<EventHandler<DisruptorBizDataEvent<String>>> eventHandlers = Lists.newArrayList();//[3]

    private DisruptorBizDataEventFactory<String> eventFactory;//[4]

    /**
     * 通用的服务于{@link DisruptorBizDataEvent}的Disruptor构造函数
     *
     * @param applicationContext Spring application context
     */
    @Autowired
    public Event1ParallelDisruptor(ApplicationContext applicationContext, DisruptorProperties properties) {
        super(
                applicationContext,
                GROUP_NAME,//[2]
                ExecutionMode.Parallel//[1]
        );
        initProperties(properties);
        this.eventFactory = new DisruptorBizDataEventFactory<>();//[4]

        //setup event handlers [3]
        this.eventHandlers.add(new DisruptorHandler1(GROUP_NAME, this.getBatchSize()));
        this.eventHandlers.add(new DisruptorHandler2(GROUP_NAME, this.getBatchSize()));
        this.eventHandlers.add(new DisruptorHandler3(GROUP_NAME, this.getBatchSize()));
    }

    @Override
    public List<? extends EventHandler<DisruptorBizDataEvent<String>>> getEventHandlers() {
        return eventHandlers;
    }

    @Override
    public DisruptorBizDataEventFactory<String> getEventFactory() {
        return eventFactory;
    }
}
```
<a name="l1oDc"></a>
#### [1]: 
在构造函数中定义支持并行模式的Disruptor引擎“ExecutionMode.Parallel”；其中AbstractBizDataEventDisruptorEngine<String>，表明该引擎中的EventHandler接受的事件为DisruptorBizDataEvent<String>，数据Payload是String类型；
<a name="VXrv3"></a>
#### [2]: 
**每个Disruptor引擎，为一个逻辑执行组，定义组名**`**GROUP_NAME="Event1-Group"**`**；同时注意该引擎在Spring容器中Bean的名称也必须为该组名**`**@Component(Event1ParallelDisruptor.GROUP_NAME)**`**；**
<a name="pLtCE"></a>
#### [3]: 
在EventHandlers容器`List<EventHandler<DisruptorBizDataEvent<String>>> eventHandlers`中绑定具体的handler，该容器中必须存放运行模式相同的handler，其必须在引擎的构造函数中执行绑定逻辑；
<a name="Z8syN"></a>
#### [4]: 
定义Event工厂，该工厂用于限定Event的类型为DisruptorBizDataEvent；
<a name="MtQTJ"></a>
### 第三步：绑定EventHandler
**代码如下：**
```java
public class DisruptorHandler1 extends AbstractParallelGroupedEventHandler<DisruptorBizDataEvent<String>> {//[1]
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(DisruptorHandler1.class);

    /**
     * 构造函数
     *
     * @param groupName
     * @param batchSize 每个批次应包含的Event数量
     */
    public DisruptorHandler1(String groupName, int batchSize) {
        super(groupName, batchSize);
    }

    @Override
    protected void doHandlerInternal(DisruptorBizDataEvent<String> event) throws Exception {//[2]
        LOGGER.info(
                "disruptor event ({})",
                event.toString()+ StrUtil.COMMA + " bizData :" + event.getBizData()
        );
    }

    @Override
    protected void doHandlerInternal(List<DisruptorBizDataEvent<String>> eventBuffer) throws Exception {//[3]
        throw new UnsupportedOperationException("not support batch processing");
    }

    @Override
    public boolean isEnabled(DisruptorBizDataEvent<String> event) {//[4]
        return "en".equals(event.getTag());
    }
}
```
<a name="kosKY"></a>
#### [1]: 
根据执行模式继承对应的抽象类`AbstractParallelGroupedEventHandler<DisruptorBizDataEvent<String>>`；注意Event的类型，必须与要绑定的Disruptor引擎实例保持一致；
<a name="qCmBi"></a>
#### [2]:
实现当event到达时的处理逻辑，注意该方法在每个event到达时都会被调用；
<a name="Pbh0n"></a>
#### [3]:
实现当有 batch size 个 event 到达时的处理逻辑，注意该方法在 Ring Buffer 中 event达到 batch size 时会被调用；另外需要注意的是`doHandlerInternal(event)`和`doHandlerInternal(events)`只应该实现一个；
<a name="sGuGV"></a>
#### [4]: 
该方法作为handler逻辑是否执行的谓词，返回false时，handler对应的逻辑将会跳过；
<a name="gwSWg"></a>
## 顺序执行模式
![](https://cdn.nlark.com/yuque/0/2022/jpeg/956807/1665737547514-503f509e-8da2-4bdd-a3de-a10bcb0f4e53.jpeg)<br />为了便于理解，图中为每个EventHandler定义了ListIndex和Order，ListIndex决定了分组顺序，按自然顺序排序，Order决定了在组中的执行顺序，按逆自然顺序排序；但对于顺序执行模式，其实并不需要ListIndex，只需要Order；<br />**第二步：根据业务逻辑选择顺序执行模式，代码如下：**
```java
@Component(Event2SerialDisruptor.GROUP_NAME)//[2]
public class Event2SerialDisruptor extends AbstractBizDataEventDisruptorEngine<Integer> {//[1]

    public final static String GROUP_NAME="Event2-Group";//[2]

    private final List<EventHandler<DisruptorBizDataEvent<Integer>>> eventHandlers = Lists.newArrayList();

    private DisruptorBizDataEventFactory<Integer> eventFactory;

    /**
     * 通用的服务于{@link DisruptorBizDataEvent}的Disruptor构造函数
     *
     * @param applicationContext Spring application context
     */
    @Autowired
    public Event2SerialDisruptor(ApplicationContext applicationContext, DisruptorProperties properties) {
        super(
                applicationContext,
                GROUP_NAME,//[2]
                ExecutionMode.SerialChain//[1]
        );
        initProperties(properties);
        this.eventFactory = new DisruptorBizDataEventFactory<>();//[4]

        //setup event handlers //[3]
        eventHandlers.add(new DisruptorHandler11(GROUP_NAME, this.getBatchSize()));
        eventHandlers.add(new DisruptorHandler12(GROUP_NAME, this.getBatchSize()));
        eventHandlers.add(new DisruptorHandler13(GROUP_NAME, this.getBatchSize()));

    }


    @Override
    public List<? extends EventHandler<DisruptorBizDataEvent<Integer>>> getEventHandlers() {
        return eventHandlers;
    }

    @Override
    public DisruptorBizDataEventFactory<Integer> getEventFactory() {
        return eventFactory;
    }

}
```
[1]: [同上节](#l1oDc)，在构造函数中定义支持顺序执行模式的Disruptor引擎“ExecutionMode.SerialChain”；<br />[2]: [**同上节**](#VXrv3)**；**<br />[3]: [**同上节**](#pLtCE)**；**<br />[4]: [**同上节**](#Z8syN)**；**<br />**第三步：实现容器中绑定的EventHandler，代码如下：**
```java
public class DisruptorHandler11 extends AbstractSerialChainGroupedEventHandler<DisruptorBizDataEvent<Integer>>{//[1]
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(DisruptorHandler11.class);

    private static final int ORDER = 3;//[5]

    public DisruptorHandler11(String groupName, int batchSize) {
        super(groupName, batchSize);
        super.order = ORDER;
    }

    @Override
    protected void doHandlerInternal(DisruptorBizDataEvent<Integer> event) throws Exception {//[2]
        LOGGER.info(
                "disruptor event ({}), result={}",
                event.toString()+ StrUtil.COMMA + " bizData :" +event.getBizData().toString(),
                event.getBizData()+ORDER
        );
        Thread.sleep(1000);
    }

    @Override
    protected void doHandlerInternal(List<DisruptorBizDataEvent<Integer>> eventBuffer) throws Exception {//[3]
        throw new UnsupportedOperationException("not support batch processing");
    }

    @Override
    public boolean isEnabled(DisruptorBizDataEvent<Integer> event) {//[4]
        return "en".equals(event.getTag());
    }
}
```
[1]: 根据执行模式继承对应的抽象类`AbstractSerialChainGroupedEventHandler<DisruptorBizDataEvent<Integer>>`；注意Event的类型，必须与要绑定的Disruptor引擎实例保持一致；<br />[2]: [**同上节**](#qCmBi)**；**<br />[3]: [**同上节**](#Pbh0n)**；**<br />[4]: [**同上节**](#sGuGV)**；**<br />**[5]：注意该模式的Handler需要定义Order，Disruptor会根据该Order的大小，越大的越先执行；**
<a name="NqgM1"></a>
## 菱形顺序执行
![](https://cdn.nlark.com/yuque/0/2022/jpeg/956807/1665737370456-6af76735-60fb-47eb-8cd8-1e0a00ed320b.jpeg)<br />为了便于理解，图中为每个EventHandler定义了ListIndex和Order，ListIndex决定了分组顺序，按自然顺序排序，Order决定了在组中的执行顺序，按逆自然顺序排序；但对于顺序执行模式，其实并不需要Order，只需要ListIndex；<br />**第二步：根据业务逻辑选择菱形顺序执行模式，代码如下：**
```java
@Component(Event3DiamondDisruptor.GROUP_NAME)//[2]
public class Event3DiamondDisruptor extends AbstractBizDataEventDisruptorEngine<Integer> {//[1]

    public final static String GROUP_NAME="Event3-Group";//[2]

    List<EventHandler<DisruptorBizDataEvent<Integer>>> eventHandlers = Lists.newArrayList();

    DisruptorBizDataEventFactory<Integer> eventFactory;

    /**
     * 通用的服务于{@link DisruptorBizDataEvent}的Disruptor构造函数
     *
     * @param applicationContext Spring application context
     */
    @Autowired
    public Event3DiamondDisruptor(ApplicationContext applicationContext, DisruptorProperties properties) {
        super(
            applicationContext, 
            GROUP_NAME, //[2]
            ExecutionMode.DependenciesDiamond//[1]
        );
        initProperties(properties);
        this.eventFactory = new DisruptorBizDataEventFactory<>();//[4]

        //[3]
        eventHandlers.add(new DisruptorHandler21(
            GROUP_NAME, 
            0, //[5]
            getBatchSize()
        ));
        eventHandlers.add(new DisruptorHandler22(GROUP_NAME, 0, getBatchSize()));
        eventHandlers.add(new DisruptorHandler31(GROUP_NAME, 1, getBatchSize()));
        eventHandlers.add(new DisruptorHandler32(GROUP_NAME, 1, getBatchSize()));
        eventHandlers.add(new DisruptorHandler41(GROUP_NAME, 2, getBatchSize()));
    }


    @Override
    public List<? extends EventHandler<DisruptorBizDataEvent<Integer>>> getEventHandlers() {
        return eventHandlers;
    }

    @Override
    public DisruptorBizDataEventFactory<Integer> getEventFactory() {
        return eventFactory;
    }

}
```
[1]: [同上节](#l1oDc)，在构造函数中定义支持菱形顺序执行模式的Disruptor引擎“ExecutionMode.DependenciesDiamond”；<br />[2]: [**同上节**](#VXrv3)**；**<br />[3]: [**同上节**](#pLtCE)**；**<br />[4]: [**同上节**](#Z8syN)**；**<br />**[5]：注意该模式的Handler需要定义listIndex，Disruptor会根据该listIndex进行分组，listIndex越小的分组越先执行；同组内的Handler并行执行；**<br />**第三步：绑定EventHandler，代码如下：**
```java
public class DisruptorHandler21 extends AbstractDiamondGroupedEventHandler<DisruptorBizDataEvent<Integer>> {//[1]
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(DisruptorHandler21.class);

    public DisruptorHandler21(String groupName, int listIndex, int batchSize) {
        super(
            groupName, 
            listIndex,//[5]
            batchSize
        );
    }

    @Override
    protected void doHandlerInternal(DisruptorBizDataEvent<Integer> event) throws Exception {//[2]
        LOGGER.info(
                "disruptor event ({}), result={}",
                event.toString()+ StrUtil.COMMA + " bizData :" +event.getBizData().toString(),
                event.getBizData()+super.getListIndex()
        );
        Thread.sleep(1000);
    }

    @Override
    protected void doHandlerInternal(List<DisruptorBizDataEvent<Integer>> eventBuffer) throws Exception {//[3]
        LOGGER.info(
                "disruptor ({}) events",
                eventBuffer.size()
        );
    }

    @Override
    public boolean isEnabled(DisruptorBizDataEvent<Integer> event) {//[4]
        return "en".equals(event.getTag());
    }
}
```
[1]: 根据执行模式继承对应的抽象类`AbstractDiamondGroupedEventHandler<DisruptorBizDataEvent<Integer>>`；注意Event的类型，必须与要绑定的Disruptor引擎实例保持一致；<br />[2]: [**同上节**](#qCmBi)**；**<br />[3]: [**同上节**](#Pbh0n)**；**<br />[4]: [**同上节**](#sGuGV)**；**<br />**[5]：注意该模式的Handler需要定义listIndex，Disruptor会根据该listIndex进行分组，listIndex越小的分组越先执行；同组内的Handler并行执行；**
<a name="Aac2g"></a>
## 链式依赖执行
![](https://cdn.nlark.com/yuque/0/2022/jpeg/956807/1665737932011-13752d99-2955-4cec-96f1-810213444beb.jpeg)<br />为了便于理解，图中为每个EventHandler定义了ListIndex和Order，ListIndex决定了分组顺序，按自然顺序排序，Order决定了在组中的执行顺序，按逆自然顺序排序；但对于顺序执行模式，其实即需要Order，也需要ListIndex；<br />**第二步：根据业务逻辑选择链式依赖执行模式，代码如下：**
```java
@Component(Event4MultiSerialDisruptor.GROUP_NAME)//[2]
public class Event4MultiSerialDisruptor extends AbstractBizDataEventDisruptorEngine<Integer> {//[1]

    public final static String GROUP_NAME="Event4-Group";//[2]

    List<EventHandler<DisruptorBizDataEvent<Integer>>> eventHandlers = Lists.newArrayList();

    DisruptorBizDataEventFactory<Integer> eventFactory;

    /**
     * 通用的服务于{@link DisruptorBizDataEvent}的Disruptor构造函数
     *
     * @param applicationContext Spring application context
     */
    @Autowired
    public Event4MultiSerialDisruptor(ApplicationContext applicationContext, DisruptorProperties properties) {
        super(
            applicationContext, 
            GROUP_NAME, //[2]
            ExecutionMode.MultiSerialChain//[1]
        );
        initProperties(properties);
        this.eventFactory = new DisruptorBizDataEventFactory<>();//[4]

        //[3]
        eventHandlers.add(new DisruptorHandler51(
            GROUP_NAME, 
            0, //[5]
            getBatchSize()
        ));
        eventHandlers.add(new DisruptorHandler52(GROUP_NAME, 0, getBatchSize()));
        eventHandlers.add(new DisruptorHandler61(GROUP_NAME, 1, getBatchSize()));
        eventHandlers.add(new DisruptorHandler62(GROUP_NAME, 1, getBatchSize()));
    }


    @Override
    public List<? extends EventHandler<DisruptorBizDataEvent<Integer>>> getEventHandlers() {
        return eventHandlers;
    }

    @Override
    public DisruptorBizDataEventFactory<Integer> getEventFactory() {
        return eventFactory;
    }

}

```
[1]: [同上节](#l1oDc)，在构造函数中定义支持菱形顺序执行模式的Disruptor引擎“ExecutionMode.MultiSerialChain”；<br />[2]: [**同上节**](#VXrv3)**；**<br />[3]: [**同上节**](#pLtCE)**；**<br />[4]: [**同上节**](#Z8syN)**；**<br />**[5]：注意该模式的Handler需要定义listIndex也需要定义Order，Disruptor会根据该listIndex进行分组，但是各分组并行执行；同组内的Handler按Order逆自然顺序执行；**<br />**第三步：绑定EventHandler，代码如下：**
```java
public class DisruptorHandler51 extends AbstractMultiSerialChainGroupedEventHandler<DisruptorBizDataEvent<Integer>>{//[1]
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(DisruptorHandler51.class);

    private static final int ORDER = 1;//[5]

    public DisruptorHandler51(String groupName, int listIndex, int batchSize) {
        super(
            groupName, 
            listIndex, //[6]
            batchSize
        );
        super.order = ORDER;
    }

    @Override
    protected void doHandlerInternal(DisruptorBizDataEvent<Integer> event) throws Exception {//[2]
        LOGGER.info(
                "disruptor event ({}), result={}",
                event.toString()+ StrUtil.COMMA + " bizData :" +event.getBizData().toString(),
                event.getBizData()+ORDER
        );
        Thread.sleep(100);
    }

    @Override
    protected void doHandlerInternal(List<DisruptorBizDataEvent<Integer>> eventBuffer) throws Exception {//[3]
        throw new UnsupportedOperationException("unsupported batch events handler");
    }

    @Override
    public boolean isEnabled(DisruptorBizDataEvent<Integer> event) {//[4]
        return "en".equals(event.getTag());
    }
}
```
[1]: 根据执行模式继承对应的抽象类`AbstractMultiSerialChainGroupedEventHandler<DisruptorBizDataEvent<Integer>>`；注意Event的类型，必须与要绑定的Disruptor引擎实例保持一致；<br />[2]: [**同上节**](#qCmBi)**；**<br />[3]: [**同上节**](#Pbh0n)**；**<br />[4]: [**同上节**](#sGuGV)**；**<br />**[5]：注意该模式的Handler需要定义listIndex也需要定义Order，Disruptor会根据该listIndex进行分组，但是各分组并行执行；同组内的Handler按Order逆自然顺序执行；**
<a name="IhEuZ"></a>
## 配置
支持细粒度的对每个DisruptorEngine进行配置：
```yaml
pg:
  disruptor:
    grouped-disruptor:
      Event1-Group: #[1]
        batch-size: 1
        ring-buffer-size: 2048 #[2]
      	multi-producer: false	#[3]
        wait-strategy: sleeping_wait	#[4]
      Event2-Group: #[1]
        batch-size: 1
        ring-buffer-size: 3000 #[2]
```
**[1]: 注意该处的Key必须与DisruptorEngine类（AbstractBizDataEventDisruptorEngine的子类）的GroupName保持一致；**<br />**[2]: ring buffer size 建议配置为2的次幂且大于1024，否则会保持1024；**<br />**[3]: multi-producer 用于指定是否都生产者，默认false；**<br />**[4]: wait-strategy 默认YIELDING_WAIT， 可取值如下：**

- _blocking_wait：是最低效的策略，但其对CPU的消耗最小并且在各种不同部署环境中能提供更加一致的性能表现；_
- _yielding_wait：性能表现跟BlockingWaitStrategy差不多，对CPU的消耗也类似，但其对生产者线程的影响最小，适合用于异步日志类似的场景；_
- _busyspin_wait：是性能最高的等待策略，同时也是对部署环境要求最高的策略。这个性能最好用在事件消费者线程比物理内核数目还要小的时候。例如：在禁用超线程技术的时候。但对于Jdk9以下的版本慎用，除非你明确的知道生产者会不断的产生事件，否则生产者和消费者线程将尽可能多的使用能使用的CPU进行空转。_
- _sleeping_wait：是可以被用在低延迟系统中的两个策略之一，这种策略在减低系统延迟的同时也会增加CPU运算量。YieldingWaitStrategy策略会循环等待sequence增加到合适的值。循环中调用Thread.yield()允许其他准备好的线程执行。如果需要高性能且事件消费者线程比逻辑内核少的时候，推荐使用YieldingWaitStrategy策略。例如：在开启超线程的时候。_
<a name="ZRtmi"></a>
# 案例代码
下载获取：disruptor-spring-boot-starter-{starter.version}-test-sources.jar，详见如下案例：DisruptorTest.java<br />并行执行模型案例：Event1ParallelDisruptor.java<br />顺序执行模型案例：Event2SerialDisruptor.java<br />菱形执行模型案例：Event3DiamondDisruptor.java<br />链式依赖执行模型案例：Event4MultiSerialDisruptor.java

<br />
