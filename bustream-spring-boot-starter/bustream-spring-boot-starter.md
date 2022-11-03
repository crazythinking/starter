该组件基于 Spring Cloud Bus 和 Spring Cloud Stream 整合，可以：

- 支持根据配置切换依赖的消息中间件，目前支持RabbitMQ，Kafka，RocketMQ；
- 支持 Message Bus 模式，进行指定目标服务的消息广播；
- 支持统一的基于消息中间件的，生产者和消费者事件驱动开发模式；
<a name="fYWek"></a>
# 基础概念
首先对Bus和Stream的概念要有所了解，才能使用好该组件。
<a name="eihQQ"></a>
## Stream
Bus也是基于Stream实现的，只是以广播的模式为基础进行；因此可以从Stream的模型来详细说明，在Stream内部有两个概念，如下图：Binder 和 Binding；<br />![](https://cdn.nlark.com/yuque/0/2021/png/956807/1626341271401-4b0e3080-7f9b-49e8-8928-1d387accbf47.png?x-oss-process=image%2Fresize%2Cw_942%2Climit_0#crop=0&crop=0&crop=1&crop=1&from=url&id=cIhvM&margin=%5Bobject%20Object%5D&originHeight=316&originWidth=942&originalType=binary&ratio=1&rotation=0&showTitle=false&status=done&style=none&title=)
<a name="lsQZy"></a>
### Binder
跟外部消息中间件集成的组件，用来创建 Binding，各消息中间件都有自己的 Binder 实现；所以Binder是负责与中间件进行交互，理论上可以是任何RPC中间件，只需要根据Binder的标准实现即可；比如 Kafka 的实现 KafkaMessageChannelBinder，RabbitMQ 的实现 RabbitMessageChannelBinder 以及 RocketMQ 的实现 RocketMQMessageChannelBinder。<br />所以在具体使用中首先要为应用定义Binder（即使用什么消息中间件、如何连接及相关配置属性等），比如在该组件的默认配置中存在三种Binder定义：

- application-bustream.kafka.binders.yml
- application-bustream.rabbit.binders.yml
- application-bustream.rocket.binders.yml

以application-bustream.rabbit.binders.yml为例：
```yaml
#RabbitMq特定的Binder(BindingServiceProperties)配置
spring:
  cloud:
    stream:
      binders:					#[1]
        #以Binder的名称为Key所对应的(BindingServiceProperties#binders->BinderProperties)
        rabbitCluster:	#[2]
          type: rabbit	#[3]
          environment:	#[4]
            spring:
              rabbitmq:
                addresses: ${spring.rabbitmq.addresses}
                cache:
                  channel:		#[6]
                    size: 10
                    checkout-timeout: 1s
                  connection:
                    mode: channel
                connection-timeout: 3s
                password: ${spring.rabbitmq.password}
                username: ${spring.rabbitmq.username}
                virtual-host: ${spring.rabbitmq.virtual-host}
  profiles:
    include:
    - bustream.rabbit.bindings.default		#[5]
```
[1]：binders为Map数据结构，所以一个应用可以定义多个binder（多个消息中间件集群），每个binder的配置对应BinderProperties；<br />[2]：binder的名称，需要确保应用内的唯一性；<br />[3]：binder的类型，由Binder实现包中的`_META-INF/spring.binders_`文件定义；<br />[4]：environment也是Map数据结构，表示任意Env属性键值对，通常应该是该binder相关的配置属性，其取决于依赖中间件的配置项定义；<br />[5]：利用spring.profiles.include导入相对应的内置的binding定义；
<a name="QZvAr"></a>
### Binding
Binding 在消息中间件与应用程序提供的 Producer 和 Consumer 之间提供了一个桥梁，而开发者只需使用应用程序的 Producer 生产 或 Consumer 消费数据即可，屏蔽了开发者与底层消息中间件的接触。<br />有了Binder之后就需要为Binder定义具体的Binding，该组件定义了多个内置的Bindings，分别按：生产者内置的channel名称，消费者内置的channel名称，消息中间件，以及binding的生产者和消费者共用配置等粒度按不同的profile定义配置文件，而所谓default，input，pollinput，output只是Binding在该组件中定义的key，Bindings在内存中以Map的数据结构保存；整个组件的Binder和Binding的参数配置关系结构比较复杂，如下图：<br />![image.png](https://cdn.nlark.com/yuque/0/2022/png/956807/1666774147909-9a7cb5a4-aa66-4d8f-b3ea-8ea31c96c42d.png#clientId=u09c383a9-7078-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=357&id=u143bca88&margin=%5Bobject%20Object%5D&name=image.png&originHeight=357&originWidth=993&originalType=binary&ratio=1&rotation=0&showTitle=false&size=35440&status=done&style=none&taskId=u211f4176-abf4-4db3-b26f-a6a89dcc1f7&title=&width=993)<br />对应该组件提供的默认配置项文件如下：
<a name="hlGyM"></a>
#### 按Binder类型维度区分的MessageChannel的内置Binding配置

- application-bustream.kafka.bindings.default.yml：当Binder的类型为Kafka时，为 default binding 配置消费者和生产者通用配置，此配置是作为Kafka Binder 的相应生产者或消费者未配置相应各配置项的兜底参数；
- application-bustream.rabbit.bindings.default.yml：当Binder的类型为RabbitMQ时，为 default binding 配置消费者和生产者通用配置，作用同上；
```yaml
# Spring cloud bus 生产者和消费者的Binding属性的默认约定配置：针对于RabbitMq的特性
spring:
  cloud:
    stream:
      #属于特定于RabbitMq的Binding(RabbitExtendedBindingProperties),不是通用的,如果涉及到mq的转换则不可用; 同样以key的方式指定配置，默认为default
      rabbit:
        default: # binding的key为default，如果未特别配置<binding-name>相关的参数，则使用此default的配置
          consumer:
            acknowledgeMode: AUTO			#【1】
            #bindingRoutingKey: testKey1 # 将queue绑定到exchange时使用的routing key。默认'#'
            # 以下配置只用于死信
            ttl: 1800000 # ms, 默认不做限制, 消息在队列中最大的存活时间; 当消息滞留超过ttl时会被当成消费失败消息, 即会被转发到死信队列或丢弃
            autoBindDlq: true # 是否自动声明死信队列(DLQ)并将其绑定到死信交换机(DLX),默认是false			#【2】
#              deadLetterExchange: ${spring.application.name}.DLX # 默认prefix+DLX, DLX的名称,prefix默认为空
#              deadLetterQueueName: ${spring.cloud.stream.rabbit.bindings.springCloudBusInput.consumer.deadLetterExchange}.dlq
#              deadLetterRoutingKey: 'packetUplinkDlxTopic.scas-data-collection.dlx' # 默认destination + group
#              dlqExpires: 30000 # 队列所有 customer 下线, 且在过期时间段内 queue 没有被重新声明, 多久之后队列会被销毁, 注意, 不管队列内有没有消息. 默认不设置
#              dlqLazy: false # 是否声明为惰性队列（Lazy Queue）.默认false
#              dlqMaxLength: 100000 # 队列中消息数量的最大限制. 默认不限制
#              dlqMaxLengthBytes: 100000000 # 队列所有消息总字节的最大限制. 默认不限制
#              dlqMaxPriority: 255 # 队列的消息可以设置的最大优先级. 默认不设置
#              dlqTtl: 1000000 # 队列的消息的过期时间. 默认不限制
            republishToDlq: true # 默认false。当为true时，死信队列接收到的消息的headers会更加丰富，多了异常信息和堆栈跟踪
#              republishDeliveryMode: DeliveryMode.PERSISTENT # 默认DeliveryMode.PERSISTENT（持久化）。当republishToDlq为true时，转发的消息的delivery mode
          producer:
            exchangeType: topic # 指定Exchange类型			#【3】
#              routingKeyExpression: headers.type # 生产者配置RabbitMq的动态路由键, 需要在生产者端组装消息时指定headers.type的值
#              routingKeyExpression: 'testKey1' 	# 固定的routing key值

```

   - [1]：基于该组件开发的消费者，建议保持自动ack，框架是对`@StreamListener`注解的方法为单位进行增强的，因此只要该方法抛出异常，就不会触发ack；
   - [2]：自动创建死信队列，当消息在配置时间内未被正常消费时，将会迁移消息到对应的死信队列，该机制主要用于防止队列阻塞；同时注意对应的死信队列也应该有相对应的消费者；
   - [3]：Binder类型是RabbitMQ时，有Exchange的概念，一般情况下建议指定使用topic类型，与其他消息中间件保持模型上的一致；
<a name="lAE4y"></a>
#### 按MessageChannel的命名维度区分的Binding配置
这个维度的配置，是不具备Binder特异性的，即无论Binder是何种类型都可适用的；并且可以通过指定`spring.cloud.stream.bindings.<channelKey>.binder`参数切换不同的Binder；

- application-stream.common.bindings.input.yml：为命名为input(MessageChannel)的Binding 配置默认参数，该配置是与Binder无关的通用部分；而站在应用服务的角度，input 是只作为Consumer的Binding；
```yaml
# Spring cloud stream 默认普通消费者的配置
spring:
  cloud:
    stream:
      bindings:
        input: # binding的key为input
#          binder: # 指定某个binder, 不指定时使用default-binder的配置			#【1】
          destination: ${gm.bustream.default-producer-name}.default			#【2】
#          group: # queue的后缀,带业务含义; 由各业务系统自身定义
          contentType: application/json		#【3】
          consumer:
            maxAttempts: 3 # 当消息消费失败时，尝试消费该消息的最大次数（消息消费失败后，发布者会重新投递）。默认3
            backOffInitialInterval: 1000 # 消息消费失败后重试消费消息的初始化间隔时间。默认1s，即第一次重试消费会在1s后进行
            backOffMultiplier: 2 # 相邻两次重试之间的间隔时间的倍数。默认2，即第二次是第一次间隔时间的2倍，第三次是第二次的2倍
            backOffMaxInterval: 10000 # 下一次尝试重试的最大时间间隔，默认为10000ms，即10s
            retryable-exceptions:
              #默认业务异常不进行重试
              net.engining.pg.support.core.exception.ErrorMessageException: false
```

   - [1]：用于指定此binding对应的Binder，此属性由应用系统本身指定，如果未指定，则使用激活的内置Binder；
   - [2]：指定 input binding 绑定的目标（Kafka 的topic，RabbitMQ的 exchange），默认由系统参数 ${gm.bustream.default-producer-name}.default 组合而成，${gm.bustream.default-producer-name}：是从消费者服务端的视角，指定其将要消费的默认Topic的前缀；
   - [3]：消息的序列化类型，与Http的类似；
- application-stream.common.bindings.output.yml：为命名为output(MessageChannel)的Binding 配置默认参数，该配置是与Binder无关的通用部分；而站在应用服务的角度，output是只作为Producer的Binding；
```yaml
# Spring cloud stream 默认生产者的配置
spring:
  cloud:
    stream:
      bindings:
        output: # binding的key为output
#          binder: # 指定某个binder, 不指定时使用default-binder的配置
          destination: ${spring.application.name}.default			#【1】
#          group: # queue的后缀,带业务含义; 由各业务系统自身定义
          contentType: application/json

```

   - [1]：指定 output binding 绑定的目标（Kafka 的topic，RabbitMQ的 exchange）；
- application-stream.common.bindings.pollinput.yml：为命名为pollinput(MessageChannel)的Binding 配置默认参数，该配置是与Binder无关的通用部分；而站在应用服务的角度，pollinput是只作为Consumer的Binding，区别于input，其获取消息的行为是按定时轮询的模式执行的；
```yaml
# Spring cloud stream 默认轮询消费者的配置
spring:
  cloud:
    stream:
      bindings:
        pollableInput:
#          binder: # 指定某个binder, 不指定时使用default-binder的配置
          destination: ${gm.bustream.default-producer-name}.default # exchange 用生产者的applicationName做前缀
#          group: # queue的后缀,带业务含义; 由各业务系统自身定义
          contentType: application/json
          consumer:
            maxAttempts: 3 # 当消息消费失败时，尝试消费该消息的最大次数（消息消费失败后，发布者会重新投递）。默认3
            backOffInitialInterval: 1000 # 消息消费失败后重试消费消息的初始化间隔时间。默认1s，即第一次重试消费会在1s后进行
            backOffMultiplier: 2 # 相邻两次重试之间的间隔时间的倍数。默认2，即第二次是第一次间隔时间的2倍，第三次是第二次的2倍
            backOffMaxInterval: 10000 # 下一次尝试重试的最大时间间隔，默认为10000ms，即10s
            retryable-exceptions:
              #默认业务异常不进行重试
              net.engining.pg.support.core.exception.ErrorMessageException: false
```
<a name="Mplsq"></a>
#### 按Binder类型+MessageChannel的命名维度区分的Binding配置

- application-stream.kafka.bindings.input.yml：为命名为input的Binding 配置默认参数，该配置特定于当Binder的类型为Kafka时有效；
- application-stream.kafka.bindings.output.yml：为命名为output的Binding 配置默认参数，该配置特定于当Binder的类型为Kafka时有效；
- application-stream.kafka.bindings.pollinput.yml：为命名为pollinput的Binding 配置默认参数，该配置特定于当Binder的类型为Kafka时有效；
- application-stream.rabbit.bindings.input.yml：与上同理；
```yaml
# Spring cloud stream 普通消费者的内置Binding属性，针对于RabbitMq的特性
spring:
  cloud:
    stream:
      #属于特定于RabbitMq的Binding,不是通用的,如果涉及到mq的转换则不可用; 同样以key的方式指定配置，默认为default
      rabbit:
        bindings:
          input:		#内置的消费者Binding名称
            consumer:
              # 自动确认
              acknowledgeMode: AUTO
              ttl: 1800000 # ms, 默认不做限制, 消息在队列中最大的存活时间; 当消息滞留超过ttl时会被当成消费失败消息, 即会被转发到死信队列或丢弃
              autoBindDlq: true # 是否自动声明死信队列(DLQ)并将其绑定到死信交换机(DLX),默认是false
              republish-to-dlq: true # 是否使用诊断标头将故障重新发布到DLQ, DLQ中的消息头上会记录异常消息; 默认false

```

- application-stream.rabbit.bindings.output.yml：与上同理；
```yaml
# Spring cloud stream 生产者的内置Binding属性，针对于RabbitMq的特性
spring:
  cloud:
    stream:
      #属于特定于RabbitMq的Binding,不是通用的,如果涉及到mq的转换则不可用; 同样以key的方式指定配置，默认为default
      rabbit:
        bindings:
          output:	#内置的生产者Binding名称
            producer:
              exchangeType: topic # 指定Exchange类型
```

- application-stream.rabbit.bindings.pollinput.yml：与上同理；
```yaml
# Spring cloud stream 默认轮询消费者的内置Binding属性，针对于RabbitMq的特性
spring:
  cloud:
    stream:
      #属于特定于RabbitMq的Binding,不是通用的,如果涉及到mq的转换则不可用; 同样以key的方式指定配置，默认为default
      rabbit:
        bindings:
          pollableInput:	#内置的消费者Binding名称
            consumer:
              acknowledgeMode: AUTO
              ttl: 1800000 # ms, 默认不做限制, 消息在队列中最大的存活时间; 当消息滞留超过ttl时会被当成消费失败消息, 即会被转发到死信队列或丢弃
              autoBindDlq: true # 是否自动声明死信队列(DLQ)并将其绑定到死信交换机(DLX),默认是false

```
<a name="lZufn"></a>
## MessageChannel
MessageChannel 是spring message模块提出的概念，从信息通讯模型上来说，Channel是与连接一一对应的；在该组件中 MessageChannel 用于接收消息，调用send方法可以将消息发送至该消息通道中；如下图：<br />![image.png](https://cdn.nlark.com/yuque/0/2022/png/956807/1666841329004-c381e60e-2de3-4d4e-b21e-8ed8dc9f8ae9.png#clientId=u09c383a9-7078-4&crop=0&crop=0&crop=1&crop=1&id=wVZJw&name=image.png&originHeight=87&originWidth=544&originalType=binary&ratio=1&rotation=0&showTitle=false&size=30228&status=done&style=none&taskId=ud8cadc18-0a6d-4b56-b7e8-e6b6c44ac8c&title=)<br />Stream组件通过`@EnableBinding`注解将预定义内置的`@Input`和`@Output`Binding，绑定到MessageChannel；`@Input` Binding 负责消费消息，`@Output`Binding 负责产生消息；该过程对于应用来说只需定义，实现由Spring Cloud Stream 框架完成；
<a name="Gg8vb"></a>
### @Input Binding
作为消息的消费者，定义如下：
```java
public interface Sink {

	/**
	 * Input channel name.
	 */
	String INPUT = "input";		//[1]

	/**
	 * @return input channel.
	 */
	@Input(Sink.INPUT)			//[2]
	SubscribableChannel input();	//[3]

}
```
[1]：Binding的命名，其值必须与配置项中binding的key保持一致，否则框架将退为使用 default binding 的配置项；<br />[2]：Binding的命名与获取消息消费者实例的方法绑定；<br />[3]：为SpringCloudStream框架指定获取SubscribableChannel的方法，由框架根据绑定的配置生成SubscribableChannel的实例对象；从该接口可以看到其实现了“subscribe”和“unsubscribe”；
```java
/**
 * A {@link MessageChannel} that maintains a registry of subscribers and invokes
 * them to handle messages sent through this channel.
 */
public interface SubscribableChannel extends MessageChannel {

	/**
	 * Register a message handler.
	 * @return {@code true} if the handler was subscribed or {@code false} if it
	 * was already subscribed.
	 */
	boolean subscribe(MessageHandler handler);

	/**
	 * Un-register a message handler.
	 * @return {@code true} if the handler was un-registered, or {@code false}
	 * if was not registered.
	 */
	boolean unsubscribe(MessageHandler handler);

}
```
<a name="wdM0p"></a>
#### Pollable @Input
区别于普通的@Input Binding，这种支持定时轮询模式拉取消息的消费者，定义如下：
```java
public interface StreamPollableInput {

    String POLLINPUT = "pollableInput";

    /**
     * 轮询的Input消息通道
     * @return PollableMessageSource
     */
    @Input(StreamPollableInput.POLLINPUT)
    PollableMessageSource pollableInput();
}
```
主要区别在于@Input绑定的方法，返回的实例是PollableMessageSource，从接口方法可以看出，最后仍然与SubscribableChannel一致，还是交给MessageHandler去获取消息；
```java
public interface PollableMessageSource extends PollableSource<MessageHandler> {
}

@FunctionalInterface
public interface PollableSource<H> {

	/**
	 * Poll the consumer.
	 * @param handler the handler.
	 * @return true if a message was handled.
	 */
	boolean poll(H handler);

	/**
	 * Poll the consumer and convert the payload to the type. Throw a
	 * {@code RequeueCurrentMessageException} to force the current message to be requeued
	 * in the broker (after retries are exhausted, if configured).
	 * @param handler the handler.
	 * @param type the type.
	 * @return true if a message was handled.
	 */
	default boolean poll(H handler, ParameterizedTypeReference<?> type) {
		return poll(handler);
	}

}
```
<a name="nS0CH"></a>
### @Output Binding
作为消息的生产者，定义如下：
```java
public interface Source {

	/**
	 * Name of the output channel.
	 */
	String OUTPUT = "output";		//[1]

	/**
	 * @return output channel
	 */
	@Output(Source.OUTPUT)		//[2]
	MessageChannel output();		//[3]

}
```
[1]：Binding的命名，其值必须与配置项中binding的key保持一致，否则框架将退为使用 default binding 的配置项；<br />[2]：Binding的命名与获取消息生产者实例的方法绑定；<br />[3]：为SpringCloudStream框架指定获取MessageChannel的方法，由框架根据绑定的配置生成MessageChannel的实例对象；从该接口可以看到其实现了“send”；
```java
@FunctionalInterface
public interface MessageChannel {

	/**
	 * Constant for sending a message without a prescribed timeout.
	 */
	long INDEFINITE_TIMEOUT = -1;


	/**
	 * Send a {@link Message} to this channel. If the message is sent successfully,
	 * the method returns {@code true}. If the message cannot be sent due to a
	 * non-fatal reason, the method returns {@code false}. The method may also
	 * throw a RuntimeException in case of non-recoverable errors.
	 * <p>This method may block indefinitely, depending on the implementation.
	 * To provide a maximum wait time, use {@link #send(Message, long)}.
	 * @param message the message to send
	 * @return whether or not the message was sent
	 */
	default boolean send(Message<?> message) {
		return send(message, INDEFINITE_TIMEOUT);
	}

	/**
	 * Send a message, blocking until either the message is accepted or the
	 * specified timeout period elapses.
	 * @param message the message to send
	 * @param timeout the timeout in milliseconds or {@link #INDEFINITE_TIMEOUT}
	 * @return {@code true} if the message is sent, {@code false} if not
	 * including a timeout of an interrupt of the send
	 */
	boolean send(Message<?> message, long timeout);

}
```
<a name="MwK1v"></a>
## Bus
与Stream在本质上是一样的，是构建在Stream之上的，但是其编程模型是基于ApplicationEvent的，并且默认以广播的模式发送消息，在SpringCloud体系中最常用的场景：微服务的配置参数发布；因此从profile配置模型上是保持一致的；<br />Binder的配置定义与Stream共用：

- application-bustream.kafka.binders.yml
- application-bustream.rabbit.binders.yml

Binding的配置定义绑定了Bus内置的`@Input(springCloudBusInput) ` 和 `@Output(springCloudBusOutput)`，定义在SpringCloudBusClient：
```java
public interface SpringCloudBusClient {

	/**
	 * Name of the input channel for Spring Cloud Bus.
	 */
	String INPUT = "springCloudBusInput";

	/**
	 * Name of the output channel for Spring Cloud Bus.
	 */
	String OUTPUT = "springCloudBusOutput";

	@Output(SpringCloudBusClient.OUTPUT)
	MessageChannel springCloudBusOutput();

	@Input(SpringCloudBusClient.INPUT)
	SubscribableChannel springCloudBusInput();

}
```
对应的配置定义：

- application-bustream.kafka.bindings.default.yml
- application-bustream.rabbit.bindings.default.yml
- application-bus.common.bindings.input.yml
- application-bus.common.bindings.output.yml
- application-bus.kafka.bindings.input.yml
- application-bus.kafka.bindings.output.yml
- application-bus.rabbit.bindings.input.yml
- application-bus.rabbit.bindings.output.yml
- application-bus.disable.yml：用于关闭bus相关的`enable`配置项，以避免相关AutoConfiguation自动加载；
```yaml
# 关闭Spring Cloud Bus 的基本能力
spring:
  cloud:
    bus:
      enabled: false
      ack:
        enabled: false
      env:
        enabled: false
      refresh:
        enabled: false
      trace:
        enabled: false

```

- application-bus.enable.yml：用于开启bus相关的`enable`配置项；
<a name="oOqmy"></a>
# 使用说明
第一步：starter依赖
```xml
<dependency>
  <groupId>net.engining.starter</groupId>
  <artifactId>bustream-spring-boot-starter</artifactId>
  <version>${starter.version}</version>
</dependency>
```
<a name="pb7Eb"></a>
## 配置
<a name="KGEtP"></a>
### Steam模式-默认配置项激活
该模式用于传统生产者-消费者开发模型，两者之间通过消息中间件进行沟通；<br />第二步：以RabbitMQ作为消息中间件为例，通过profile激活默认配置；

- `bus.disable`：当不需要消息以广播模式传播时，需要激活该profile，通常在Stream模式下都不需要，除非必须使用基于ApplicationEvent驱动的开发模式时，可以考虑使用Stream+Bus的组合模式；
- `channel.stream.input.rabbit`：当应用系统需要消费者，并使用RabbitMQ作为Binder，且使用默认 input Binding 时，需要激活该profile，该动作激活默认配置文件`application-channel.stream.input.rabbit.yml`：
```yaml
spring:
  profiles:
    include:
    - bustream.rabbit.binders
    - stream.common.bindings.input
    - stream.rabbit.bindings.input

```
如上，同时再激活`application-bustream.rabbit.binders.yml, application-stream.common.bindings.input.yml, application-stream.rabbit.bindings.input.yml`；

- `channel.stream.output.rabbit`：当应用系统需要生产者，并使用RabbitMQ作为Binder，且使用默认 output Binding 时，需要激活该profile，该动作激活默认配置文件`application-channel.stream.output.rabbit.yml`：
```yaml
spring:
  profiles:
    include:
    - bustream.rabbit.binders
    - stream.common.bindings.output
    - stream.rabbit.bindings.output
```

- `channel.stream.pollinput.rabbit`：当应用系统需要定时轮询模式的消费者，并使用RabbitMQ作为Binder，且使用默认 pollinput Binding 时，需要激活该profile，该动作激活默认配置文件`application-channel.stream.pollinput.rabbit.yml`：
```yaml
spring:
  profiles:
    include:
    - bustream.rabbit.binders
    - stream.common.bindings.pollinput
    - stream.rabbit.bindings.pollinput

```
其他类型Binder，只需激活相对应的类似命名的profile配置即可，如以Kafka为消息中间件时，就激活：

- `channel.stream.input.kafka`
- `channel.stream.output.kafka`
- `channel.stream.pollinput.kafka`
<a name="WF42t"></a>
### Bus模式-默认配置项激活
该模式用于事件驱动开发模型；同样以RabbitMQ作为消息中间件为例，通过profile激活默认配置；

- `channel.bus.input.rabbit`：当应用系统需要消费者，并使用RabbitMQ作为Binder，且使用默认 springCloudBusInput Binding 时，需要激活该profile，该动作激活默认配置文件`application-channel.bus.input.rabbit.yml`：
```yaml
spring:
  profiles:
    include:
    - bus.enable
    - bustream.rabbit.binders
    - bus.common.bindings.input
    - bus.rabbit.bindings.input

```

- `channel.bus.output.rabbit`：当应用系统需要生产者，并使用RabbitMQ作为Binder，且使用默认 springCloudBusOutput Binding 时，需要激活该profile，该动作激活默认配置文件`application-channel.bus.output.rabbit.yml`：
```yaml
spring:
  profiles:
    include:
    - bus.enable
    - bustream.rabbit.binders
    - bus.common.bindings.output
    - bus.rabbit.bindings.output

```
其他类型Binder，只需激活相对应的类似命名的profile配置即可，如以Kafka为消息中间件时，就激活：

- `channel.bus.input.kafka`
- `channel.bus.output.kafka`
<a name="ouUtO"></a>
### 自定义配置
第三步，理论上默认配置已经足够，自定义配置的目的是为了应对不同场景下 Binder和Binding 需要微调，所以强烈建议按 Stream、Bus、Binder、Binding，4个维度定义配置项文件，最后通过组合profile来归一到相应的channel来应对不同的应用场景；

1. 根据运行环境定义Binder相关配置；如在dev环境使用RabbitMQ时，定义`application-rabbit.dev.properties`：此处的配置将被用于填充 rabbitCluster Binder 配置的占位符（此为该组件下RabbitMQ类型的默认Binder，如果需要定义该类型其他命名的Binder，则需要参考`application-bustream.rabbit.binders.yml`定义完整的Binder配置文件；在一个应用系统中允许定义多个Binder，虽然并不常用）；另外还需注意`spring.rabbitmq.xxx`系列的配置参数不会直接影响Binder，需要配置在Binder之下（参考application-bustream.rabbit.binders.yml的配置模式）。
```properties
spring.rabbitmq.virtual-host=spring-cloud-bus.host

spring.rabbitmq.addresses=ubuntu.wsl:5683,ubuntu.wsl:5684,ubuntu.wsl:5692
spring.rabbitmq.username=bustest
spring.rabbitmq.password=bustest

```

2. 定义Stream和Bus相关的common配置；如定义`application-bustream.common.properties`：此处的配置将被用于覆盖Stream和Bus维度的默认配置，以及指定作为消费者的Binding将要消费的Topic（对于RabbitMQ即为Exchange）名称（按规范要求，每个消息生产者的服务端都应发送消息到独立的Topic/Exchange）；
```properties
#从消费者服务端的视角，指定其将要消费的Topic的前缀
gm.bustream.default-producer-name=${spring.application.name}
#当需要从多个Topic消费消息时，需要根据Topic命名规范配置Topic的前缀，通常应该是生产者应用名称
gm.bustream.other-producer-names.scac=scac-online-sv
gm.bustream.other-producer-names.sccust=sccust-online-sv

## 属于 Stream Binding的通用配置
# 指定默认binder，未定义binder的channel将使用该binder
spring.cloud.stream.default-binder=rabbitCluster
#spring.cloud.stream.default-binder=kafkaCluster

```
注意：spring.cloud.stream.default-binder 必须在应用系统中显示指定，其值取决于Binder的类型，默认情况下RabbitMQ可以使用rabbitCluster，Kafka可以使用kafkaCluster，如果自定义其他命名的Binder，也可以使用；该值将作为所有Binding未特别指定`spring.cloud.stream.bindings.<binding-name>.binder`参数时的兜底配置；

3. 定义Binding相关的common配置，如`application-channel.input.common.properties, application-channel.output.common.properties`；
```properties
### 消费者（channel-name/binding-name: input） 的相关配置
#为Channel指定Binder，这将决定Channel使用哪个MQ，如果不指定默认为default-binder
#spring.cloud.stream.bindings.input.binder=kafkaCluster
spring.cloud.stream.bindings.input.binder=rabbitCluster

## 属于 Stream Binding的通用配置
#queue的后缀, 必须带有业务含义；在topic模式下其还作为分组
spring.cloud.stream.bindings.input.group=repayBack

## 属于特定于RabbitMq的Binding,不是通用的(如果涉及到mq的转换则不可用; 同样以key的方式指定配置，默认为spring.cloud.stream.default-binder);
#指定exchange类型direct的相应的配置，不推荐使用，默认使用topic
#spring.cloud.stream.rabbit.bindings.input.consumer.exchangeType=direct
#spring.cloud.stream.rabbit.bindings.input.consumer.bindQueue=true
#spring.cloud.stream.rabbit.bindings.input.consumer.queueNameGroupOnly=true
#spring.cloud.stream.rabbit.bindings.input.consumer.declareExchange=false
#指定消息进入死信队列的间隔时间
#spring.cloud.stream.rabbit.bindings.input.consumer.ttl=200000
#只有与指定的routingKey相同的消息才会从exchange转发到该consumer对应的queue; 不配置时routingKey为“#”
#spring.cloud.stream.rabbit.bindings.input.consumer.binding-routing-key=${spring.cloud.stream.bindings.input.group}

## 属于特定于Kafka的Binding,不是通用的(如果涉及到mq的转换则不可用; 同样以key的方式指定配置，默认为spring.cloud.stream.default-binder);
#spring.cloud.stream.kafka.bindings.input.consumer.configuration.

```
```properties
### 生产者（channel name: output） 的相关配置
#为Channel指定Binder，这将决定Channel使用哪个MQ
#spring.cloud.stream.bindings.output.binder=kafkaCluster
spring.cloud.stream.bindings.output.binder=rabbitCluster

## 属于 Stream Binding的通用配置
#生产者必须确保将消息传递到这些组，即使这些组在消息创建之后才开始传递；
# 此处有坑，当相同binder的生产者和消费者在同一进程，如果生产者的CachingConnectionFactory先创建，
# 会引发后续消费者创建Connection时“protocol method”中的参数不一致，如’x-message-ttl‘等；但通常相同binder的生产者和消费者不会出现在同一进程；
#spring.cloud.stream.bindings.output.producer.required-groups[0]=repayBack
#消息生产者的分区设置
#spring.cloud.stream.bindings.output.producer.partitionKeyExpression=payload.userId
#spring.cloud.stream.bindings.output.producer.partitionKeyExpression=headers.id

## 属于特定于RabbitMq的Binding,不是通用的(如果涉及到mq的转换则不可用; 同样以key的方式指定配置，默认为default-binder);
#spring.cloud.stream.rabbit.bindings.output.producer.routing-key-expression=header.messageType

## 属于特定于Kafka的Binding,不是通用的(如果涉及到mq的转换则不可用; 同样以key的方式指定配置，默认为default-binder);


```

4. 定义不同环境下Binding相关的配置`application-channel.input.dev.properties, application-channel.output.dev.properties`；
```properties
#激活组合的profiles
spring.profiles.include=bus.disable,channel.stream.input.rabbit,rabbit.dev,bustream.common,channel.input.common

#根据环境不同的配置项
## 属于 Stream Binding的通用配置
# 初始/最少/空闲时 消费者数量, 默认1
#spring.cloud.stream.bindings.input.consumer.concurrency=1

#属于特定于RabbitMq的Binding,不是通用的,如果涉及到mq的转换则不可用; 同样以key的方式指定配置，默认为default
# 默认1, queue的消费者的最大数量。当前消费者数量不足以及时消费消息时, 会动态增加消费者数量, 直到到达最大数量, 即该配置的值.
#spring.cloud.stream.rabbit.bindings.input.consumer.maxConcurrency=1
# 限制consumer在消费消息时, 一次能同时获取的消息数量, 默认1
#spring.cloud.stream.rabbit.bindings.input.consumer.prefetch=1
#spring.cloud.stream.rabbit.bindings.input.consumer.acknowledge-mode=manual


```
```properties
#激活组合的profiles
spring.profiles.include=bus.disable,channel.stream.output.rabbit,rabbit.dev,bustream.common,channel.output.common

#根据环境不同的配置项
## 属于 Stream Binding的通用配置
#消息生产者的分区设置
#spring.cloud.stream.bindings.output.producer.partitionCount=2

```
<a name="LJjuQ"></a>
## 产生消息
无论采用何种消息中间件，其在开发模型上都是一致的，因此AbstractProduceBustreamHandler是无感于消息中间件的，其实现类都是可以通过Binding的配置无缝切换到不同的消息中间件；
<a name="GF8ff"></a>
### Stream模式
第四步：定义与Producer对应的AbstractProduceBustreamHandler，并且与对应的Binding绑定，内置的Producer Binding 是 output-Binding；<br />对于同一个Producer-Binding，其只能将消息发送到`spring.cloud.stream.bindings.<binding-name>.destination`配置指定的Topic/Exchange；另外AbstractProduceBustreamHandler的实现可以支持泛型 Message Payload，其可以通过指定不同的泛型实现，向同一个Topic/Exchange发送不同结构体的消息；因此针对内置的 output-Binding 该组件又抽象了AbstractOutputBustreamHandler，其接受可序列化的泛型Message Payload；
```java
/**
* 默认消息生产者的处理抽象类，由业务类继承并注入 Spring Context；
* 注意：此类绑定了Channel -> 内置的OUTPUT-Binding；如果业务类需要自定义的Binding时，需要自行实现{@link AbstractProduceBustreamHandler}；
*
**/
public abstract class AbstractOutputBustreamHandler<E extends Serializable> extends AbstractProduceBustreamHandler<E> {

    @Autowired
    @Qualifier(Source.OUTPUT)		//绑定Channel -> OUTPUT-Binding
    @Override
    protected void setMessageChannel(MessageChannel messageChannel) {
        super.setMessageChannel(messageChannel);
    }

}
```
如相应的在应用系统中如果要产生 User 和 User2 两种消息，则分别需要实现UserOutputStreamHandler 和 User2OutputStreamHandler；
```java
@Profile({
        "channel.output.common",	//[1]
})
@Service
public class UserOutputStreamHandler extends AbstractOutputBustreamHandler<User> implements InitializingBean {	//[2]

    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserOutputStreamHandler.class);

    @Override
    protected void transform(User event, Map<String, Object> headers) {		//[3]
        LOGGER.info("transform event to user object: {}", event);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.setLogger(LOGGER);		//[4]
    }
}
```
```java
@Profile({
        "channel.output.common",		//[1]
})
@Service
public class User2OutputStreamHandler extends AbstractOutputBustreamHandler<User2> implements InitializingBean {	//[2]

    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(User2OutputStreamHandler.class);

    @Override
    protected void transform(User2 event, Map<String, Object> headers) {	//[3]
        LOGGER.info("transform event to user2 object: {}", event);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.setLogger(LOGGER);		//[4]
    }
}
```
[1]：这里建议指定要激活的profile，限制只有在其依赖的相应配置项被激活时才注入这个StreamHandler；<br />[2]：指定产生消息的泛型类；<br />[3]：实现该方法：对要发送的消息进行自定义的数据转换处理逻辑，此方法将在消息被发送前被调用；<br />[4]：设置logger实例，注意这一步必须设置的，否则日志打印的类标识将回退到父类；<br />最后在需要发送消息的地方，获取相应的StreamHandler-Bean，并调用send方法；
```java
// ......

@Autowired
User2OutputStreamHandler user2MsgOutputStreamHandler;

// ......

Map<String, Object> headerMap2 = Maps.newHashMap();
User2 user2 = setupUser2();
user2.setAge(0);
headerMap2.put("messageType", "type2");
user2MsgOutputStreamHandler.send(user2, headerMap2);

// ......
```
<a name="ZWqzF"></a>
### Bus模式
待补充
<a name="pJcQv"></a>
## 消费消息
无论采用何种消息中间件，其在开发模型上都是一致的，因此AbstractConsumeBustreamHandler也是无感于消息中间件的，其实现类都是可以通过Binding的配置无缝切换到不同的消息中间件；
<a name="bDM9J"></a>
### Stream模式
第五步：定义与Consumer对应的AbstractConsumeBustreamHandler，并且与对应的Binding绑定，内置的Consumer Binding 是 input-Binding 和 pollinput-Binding；<br />对于同一个Consutmer-Binding，其只能消费`spring.cloud.stream.bindings.<binding-name>.destination`配置指定的Topic/Exchange的消息；注意消费者端必须通过`spring.cloud.stream.bindings.<binding-name>.group`，如果consumer端不指定group时，由于Consumer@SpringCloudStream 默认是以匿名方式在RabbitMq上创建临时Queue，那么Producer端产生的消息将会在Consumer端断开时丢失（临时Queue随着Consumer下线而自动删除）；<br />另外与生产者的开发模式稍有不同，对于依赖框架组件本身来控制消息消费Ack过程的情况，同时也建议由组件本身来控制，消费者只需要直接实现AbstractConsumeBustreamHandler，并指定 Message Payload 的具体Pojo类型；组件本身会捕捉添加了@StreamListener注解的consume方法只需异常，如果未抛出任何异常，则会提交Ack，否则提交Nack（注意：对于使用RabbitMQ来说保持配置项`spring.cloud.stream.rabbit.bindings.<binding-name>.consumer.acknowledgeMode=true`或`spring.cloud.stream.rabbit.bindings.default.consumer.acknowledgeMode=true`）；
<a name="kRFd7"></a>
#### 通用消费者实现案例
```java
@Profile({
        "channel.input.common",		//[1]
})
@Service
public class UserInputStreamHandler extends AbstractConsumeBustreamHandler<User> implements InitializingBean {		//[2]
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserInputStreamHandler.class);

    public List<User> users = new ArrayList<>();

    public AtomicInteger okCount = new AtomicInteger(0);

    /**
     * 接收消息，无需显示调用，由监听器自动触发
     */
    @StreamListener(value = Sink.INPUT)
    protected void consume4ChannelInput(@Payload User event, @Headers MessageHeaders headers) {		//[3]
        super.consume(event, headers);
    }

    @Override
    protected void handler(User event, MessageHeaders headers) throws Exception{			//[4]
        LOGGER.info("handler event for user1 object: {}", event);
        if (event.getAge()==0){
            throw new ErrorMessageException(ErrorCode.SystemError, "error test");
        }
        else {
            users.add(event);
            okCount.incrementAndGet();
        }
    }		

    @Override
    public void afterPropertiesSet() throws Exception {		//[5]
        super.setLogger(LOGGER);
    }
}
```
[1]：这里建议指定要激活的profile，限制只有在其依赖的相应配置项被激活时才注入这个StreamHandler；<br />[2]：指定消费消息的泛型类；<br />[3]：使用@StreamListener注解标识该方法需要被SpringCloudStream增强并且绑定到指定的Binding，通过@Payload获取指定类型的消息，通过@Headers获取MessageHeaders（此处的使用与 MVC Controller 的模式类似）；该方法内逻辑是模板化的，只需要调用父类的consume方法即可；另外注意@StreamListener和@Payload的详细用法，参考官方文档；<br />[4]：handler方法是真正需要应用系统去实现业务逻辑的方法，注意该方法不要做任何try-catch处理，主动或被动抛出异常，由上层框架去处理，除非明确需要在某些情况下丢弃消息；<br />[5]：设置logger实例，注意这一步必须设置的，否则日志打印的类标识将回退到父类；
<a name="stinM"></a>
#### 轮询消费者实现案例
与通用消费者不同，该类消费者首先需要定义一个与特定轮询Binding绑定的抽象类，如下：
```java
/**
 * 默认消息轮询消费者的处理抽象类，由业务类继承并注入 Spring Context；
 * 注意：此类绑定了Channel -> 内置的POLLINPUT-Binding；如果业务类需要自定义的Binding时，需要自行实现{@link AbstractConsumeBustreamHandler}；
 *
 **/
public abstract class AbstractPollinputBustreamHandler<E extends Serializable>
                                                    extends AbstractConsumeBustreamHandler<E> {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPollinputBustreamHandler.class);

    PollableMessageSource pollableMessageSource;

    public AbstractPollinputBustreamHandler() {			//[1]
        super();
        super.setLogger(LOGGER);
        super.setType(Type.POLLABLE_CONSUMER);
    }

    @Autowired
    @Qualifier(StreamPollableInput.POLLINPUT)		
    public void setPollableMessageSource(PollableMessageSource pollableMessageSource) {			//[2]
        this.pollableMessageSource = pollableMessageSource;
    }

    @Scheduled(fixedDelayString ="${gm.bustream.pollinput-fixed-delay:1000}")			//[3]
    public void pollConsume4ChannelPollinput(){
        pollableMessageSource.poll(
                message -> {
                    super.consume((E) message.getPayload(), message.getHeaders());
                },
                getParameterizedTypeReference()
        );

    }

    /**
     * 由业务子类用于指定具体的引用类型
     * @return ParameterizedTypeReference
     */
    protected abstract ParameterizedTypeReference getParameterizedTypeReference();		//[4]

}
```
[1]：构造函数中必须指定logger和type的元信息；<br />[2]：绑定POLLINPUT-Binding，并从SpringContext中获取与 binding-name 绑定的 PollableMessageSource；<br />[3]：定义轮询线程的执行间隔时间，绑定到自定义的外部配置项，注意必须提供默认值；<br />[4]：定义抽象方法，由业务子类用于指定消息的泛类型；
```java
@Profile("channel.pollinput.common")		//[1]
@Service
public class UserPollInputStreamHandler extends AbstractPollinputBustreamHandler<User> implements InitializingBean {	//[2]
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserPollInputStreamHandler.class);

    public List<User> users = Lists.newArrayList();

    @Override
    protected ParameterizedTypeReference<User> getParameterizedTypeReference() {
        return ParameterizedTypeReference.forType(User.class);					//[3]
    }

    @Override
    protected void handler(User event, MessageHeaders headers) throws Exception {
        LOGGER.info("handler event for user object: {}", event);
        users.add(event);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.setLogger(LOGGER);
    }
}
```
[1]：这里建议指定要激活的profile，限制只有在其依赖的相应配置项被激活时才注入这个StreamHandler；<br />[2]：指定消费消息的泛型类；<br />[3]：实现抽象方法，为父类指定泛型参数的实际类型；
<a name="VXstb"></a>
### Bus模式
待补充
<a name="ZRtmi"></a>
# 案例代码
下载获取：bustream-spring-boot-starter-{starter.version}-test-sources.jar，详见如下案例：

- StreamRabbitTestCase.java
- StreamKafkaTestCase.java
- StreamPollableTestCase.java
- StreamTestCase.java
- BusTestCase.java
