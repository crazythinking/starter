该组件是简化基于TransFlow引擎开发交易链的starter封装，基于模板责任链设计模式，实现Invoker原子化，Flow组装化；支持系统层交易幂等（基于数据库唯一索引：channel+serialNo），支持交易断续，支持Invoker条件跳过，支持Inbound和Outbound交易流水数据异步复制到存储层等功能；<br />Trans Flow 交易责任链引擎：TransFlow 来源于V+系统，是信用卡核心（单双信息交易处理）专属高频交易流编排模式；从设计规范及开发流程上驱动开发人员保持单一性的原子交易设计原则；同时框架底层提供内置的通用公共原子交易，如：记录交易流水、交易合规性检查、事件分割器、交易断续、异步并行、监听器等；Trans Flow一方面提升开发人员的效率和质量，另一方面保障了系统运行稳定、健壮及快速排障。<br />TransFlow引擎的总体架构如下图：<br />![image.png](https://cdn.nlark.com/yuque/0/2022/png/956807/1665976387850-ae87d9dc-4f45-4556-a34e-a3229036ffbc.png#clientId=uc192f093-451f-4&crop=0&crop=0&crop=1&crop=1&from=paste&id=u0c364587&margin=%5Bobject%20Object%5D&name=image.png&originHeight=689&originWidth=487&originalType=binary&ratio=1&rotation=0&showTitle=false&size=45373&status=done&style=none&taskId=uee493b99-e09e-488d-8cf6-f5853455116&title=)<br />TransFlow定义的一般性原则：

- Flow定义中不可包含业务逻辑，除非要Override父类AbstractFlow中的方法，但一般不推荐；
- Invoker的设计必须遵循“单一职责”原则，保持Invoker逻辑的单一简单化；
- 对于大部分业务联机系统而言，为了确保信息最终一致性，建议必须记录Inbound（内置Invoker），如果有系统外调，必须记录OutBound（内置Outboundable接口默认实现）；

如果TransFlow中配置的Invoker实现了Outboundable或CheckPoint接口，那么该Trans就具备了支持断续交易的能力：
> 所谓支持断续交易即当实现了Outboundable或CheckPoint接口的Invoker，在执行过程中发生意外中断后，该Trans的状态将标记为Processing，且持久化该Trans中断时的FlowContext；后续通过专门定制的补偿TransFlow（CallBackOutboundFlow 或 CompensationResultFlow）完成中断Invoker的正常逻辑；之后当该笔Trans被再次触发（通过幂等机制）时，将从中断的Invoker位置继续完成后续执行；

而对于支持断续交易的TransFlow定义需要遵循如下约定：

- ContextKey的泛型必须是有默认构造函数的Pojo，用于支持序列化与反序列化；
- 业务层的自定义Invoker大部分情况下必须是继承AbstractSkippableInvoker的子类；
- 对于异步的父级Invoker，不可实现Outboundable或CheckPoint接口；根据Invoker的设计原则（单一、简单），父级Invoker在设计语义只应用于获取异步子级Invoker的返回值并在内存中处理逻辑；
- 对于异步的子级Invoker不允许再次嵌套异步Invoker，即只允许一层嵌套，多层嵌套在逻辑上也没有意义；
- 非异步的Invoker，必须通过抛出异常来引发Break；异步的Invoker只支持实现Outboundable的，且显示的设置为异步；
<a name="oOqmy"></a>
# 使用说明
第一步：starter依赖
```xml
<dependency>
  <groupId>net.engining.starter</groupId>
  <artifactId>transflow-spring-boot-starter</artifactId>
  <version>${starter.version}</version>
</dependency>
```
<a name="NLM2z"></a>
# 配置
第二步：全局配置主要包括上下文调试信息的开关以及异步Invoker使用的线程池配置；
```yaml
gm:
  trans-flow:
    dump-procedure: true							#[1]
    dump-request: true								#[2]
    dump-response: true								#[3]
    async-excutor-cole-pool-size:			#[4]
    async-excutor-keep-alive:					#[5]
    async-excutor-max-pool-size:			#[6]
    async-excutor-queue-capacity:			#[7]
    enabled: 													#[8]
```
[1]：dump-procedure，是否打印Inovker执行前后的上下文明细，默认true；<br />[2]：dump-request，是否打印Trans请求进入时的的上下文明细，默认true；<br />[3]：dump-response，是否打印Trans执行完成后的上下文明细，默认true；<br />[4]：async-excutor-cole-pool-size，用于异步Invoker执行线程池的核心线程数大小，默认10；<br />[5]：async-excutor-keep-alive，用于异步Invoker执行线程池的线程存活时长（单位秒），默认10；<br />[6]：async-excutor-max-pool-size，用于异步Invoker执行线程池的最大线程数大小，默认100；<br />[7]：async-excutor-queue-capacity，用于异步Invoker执行线程池的队列大小，默认100；<br />[8]：enabled，是否开启TransFlow组件，默认true；
<a name="Np6GN"></a>
# 定义TransFlow
第三步：定义TransFlow；
```java
@FlowDefinition(
        name = "示列流程",
        desc = "示列流程. 用于测试",
        invokers = {
                WriteInboundJournal.class,	//[1]
                TransactionSeperator.class,	//[2]
                SampleInvoker1.class,		//[3]
                SampleInvoker2.class,
                //SampleInvoker3.class,
                TransactionSeperator.class,
                SampleInvoker4.class,
                TransactionSeperator.class,
                DetermineFinalResult.class,		//[4]
                WriteJournalUpdateResult.class	//[5]
        },
        response = {							//[6]
                IntValue2Key.class
        }
)
@Service
public class SampleFlow extends AbstractFlow {}
```
[1]: WriteInboundJournal，通用Invoker，用于持久化Inbound流水；一般当Trans进入服务时，记录完整的交易数据是后续支持幂等，断续交易等功能的基础，通常配置为整个TransFlow的第一步；另外由于是通用框架，所以交易报文对于框架来说都是非结构化数据，因此框架只提取部分通用报文（渠道号、渠道流水号、全局流水号，交易时间等），其他报文都作为非结构化数据存入CtBoundLob；<br />[2]: TransactionSeperator，通用Invoker，作为Invoker分组的占位标志，一组内的多个Invoker属于一组，TransFlow会以组为单位进行事务提交，当组内某个Invoker发生异常时，该组内所有已完成的操作将会发生事务回滚；<br />[3]:  自定义Invoker；<br />[4]: DetermineFinalResult，通用Invoker，根据FlowContext中是否存在异常信息，判定Trans的最终执行状态（FinalResult.Success 或 FinalResult.Failed）；<br />[5]: WriteJournalUpdateResult，通用Invoker，根据FlowContext中FinalResult，持久化Inbound的最终执行状态以及ResponseData；<br />[6]: 定义TransFlow的返回值包含的字段，注意最小化输出原则，非必要的字段不要定义，以减少持久化的容量；<br />另外注意准确定义@FlowDefinition的属性：name和desc，其主要用于调试与日志中的显示，便于后期维护的维护与识别；
<a name="xCgkg"></a>
# 定义Invoker
第四步：定义Invoker；
```java
@InvokerDefinition(
	name = "square invoker2",
	requires = IntValue2Key.class,	//[1]
	results = IntValue2Key.class	//[2]
)
public class SampleInvoker2 extends AbstractSkippableInvoker	//[5]
{
	@Override
	public void invoke(FlowContext ctx)	//[3]
	{
		int value = ctx.get(IntValue2Key.class);
		value = value * value;
		
		ctx.put(IntValue2Key.class, value);

		throw new ErrorMessageException(ErrorCode.SystemError, "SampleInvoker2 test for ErrorMessages");
	}

	@Override
	protected boolean skip2Customrized(Map<String, String> parameters) {//[4]
		return ValidateUtilExt.isNotNullOrEmpty(parameters.get("skip"));
	}

}
```
[1]：requires，定义该Invoker必输参数；TransFlow引擎运行该Invoker时会对requires定义的入参进行检查，在FlowContext中检查该入参是否为空，如果为空则中断Trans，并抛出NullPointerException至FlowContext；另外还可通过配置optional，指定非必输参数，引擎不会对此做任何检查；<br />[2]：results，定义该Invoker的输出参数；TransFlow引擎只对异步子级Invoker的results进行钻取，由于异步子级Invoker会有自己独立的FlowContext，因此FlowContext中没有在results中定义出参，就不会被暴露到父级的FlowContext；另外在使用control-maven-plugin生成 TransFlow Graph 时，会依赖此配置，故建议合理配置此项，同样建议保持最小输出原则；<br />[3]：实现invoker方法，该方法是业务逻辑的唯一入口，从FlowContext获取该Invoker业务逻辑需要的入参，要输出的参数，直接存入FlowContext即可；另外由于所有Invoker会被TransFlow引擎注册到Spring容器，因此也可通过依赖注入机制获取Spring中的Bean；<br />[4]：可选的覆盖skip2Customrized方法，该方法用于自定义跳过逻辑，返回true时，将会跳过该Invoker的业务逻辑（invoker方法），该方法只能通过FlowContext中的内置参数（parameters字段）中数据进行跳过逻辑的判定；另外该方法的跳过逻辑是skippable方法的补充，为自定义逻辑留的后门，通常如果前序的Invoker执行过程中触发了TransFlow引擎本身的跳过逻辑时，将直接跳过，而如果内置参数“NOT_SUCCESS”为空，才会执行skip2Customrized方法；
```java
public boolean skippable(Map<String, String> parameters) {
    // 判断trans flow 内置的参数NOT_SUCCESS，用于标识：必须成功执行完成的前序Invoker是否都执行成功;
    // 默认情况为：不成功时会设置该参数，成功时不设置该参数（或者也可以设置为FinalResult.Success.getValue()）；
    String notSuccess = parameters.get(FlowContext.CONS_PARAMETERS.NOT_SUCCESS.name());
    if (ValidateUtilExt.isNullOrEmpty(notSuccess)) {
        // 判断实现类的跳过逻辑
        if (skip2Customrized(parameters)) {
            return true;
        }
        return false;
    }
    else if (notSuccess.equals(FinalResult.Repeat.getValue())) {
        //重复交易，进行跳过
        return true;
    }
    else if (notSuccess.equals(FinalResult.Break.getValue())){
        //中断交易，进行跳过
        return true;
    }
    else if (notSuccess.equals(FinalResult.Processing.getValue())) {
        //处理中，不进行跳过
        //还要看快照所在的位置，快照中已执行成功的invoker都应该跳过
        String succeedInvokers = parameters.get(FlowContext.CONS_PARAMETERS.SUCCEED_INVOKERS.name());
        if (ValidateUtilExt.isNotNullOrEmpty(succeedInvokers)){
            return succeedInvokers.contains(this.getClass().getName());
        }
        return false;
    }
    else {
        //只有被设置为不等于FinalResult.Success.getValue()时可以跳过
        return !notSuccess.equals(FinalResult.Success.getValue());
    }
}
```
[5]：一般情况下，自定义Invoker都必须继承AbstractSkippableInvoker，该抽象类实现了Invoker和Skippable接口，分别对应invoker和skippable方法；
<a name="dx90v"></a>
## 异步Invoker
普通的TransFlow模型都是串行顺序执行的，但为了提高整体Trans的执行效率，当某些Invoker之间相互不依赖时，可以改为并行执行（例如：需要调用多个外部接口，并根据多个外部接口的返回聚合后进行后续处理），该异步处理是典型的Fork-Join模型；
```java
@InvokerDefinition(
        name = "fork-join invoker4",
        requires = IntValue1Key.class,
        asynSubInvokers = {				//[1]
                SampleInvoker5.class,
                SampleInvoker6.class
        }
)
public class SampleInvoker4 extends AbstractSkippableInvoker {

    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(SampleInvoker4.class);

    @Override
    public void invoke(FlowContext ctx) {
        LOGGER.debug("get value from asyn sub invoker SampleInvoker5=[{}]", ctx.get(StringValue1Key.class));	//[2]
        LOGGER.debug("get value from asyn sub invoker SampleInvoker6=[{}]", ctx.get(StringValue2Key.class));
    }

}
```
[1]：asynSubInvokers，配置需要并行执行的子级Invoker，子级Invoker的定义模式与上一节的普通Invoker一致；<br />[2]：父级Invoker会等待所有子级Invoker执行完成（注意：此处为了确保获取返回和简化异步模型的复杂性，会无限等待子级Invoker的返回，不会在父级线程中设置超时，超时应该由子级Invoker本身的业务逻辑中进行控制），才会回到父级Invoker的invoke方法，此时FlowContext中可以获取子级Invoker返回的数据；
<a name="KOfrM"></a>
# 交易断续
第五步：对于实现了Outboundable或CheckPoint的Invoker，都会支持交易断续；交易断续的实现原理是：

1. 在该Invoker执行的过程中被动（运行时异常）或主动（根据业务逻辑需要抛出业务异常）发生中断时，对此时的FlowContext做快照处理并持久化；
2. 后续通过触发相应的补偿交易（CallBackOutboundFlow或CompensationResultFlow），补偿当前中断的Invoker原本所需要存入FlowContext的数据；
3. 最后通过主动（一般是轮询任务）或被动（由外部系统重试）触发交易幂等，继续执行后续的Invoker；

两种接口分别对应不同的场景：

- CheckPoint：针对虽然是本地逻辑，但需要延迟一段时间才能获得结构的Invoker（典型场景检查文件是否到达）；需要通过对应的内置补偿交易CompensationResultFlow，补偿当前中断的Invoker原本所需要存入FlowContext的数据；
- Outboundable：只用于需要调用外部服务的Invoker，该类Invoker会在Outbound记录外调流水数据；需要通过对应的内置补偿交易CallBackOutboundFlow，补偿当前中断的Invoker原本所需要存入FlowContext的数据；
<a name="Ifmto"></a>
## CheckPoint
实现了CheckPoint接口的Invoker通常只需要实现result4Compensate方法，其作用是从补偿信息中处理加工返回值，并存入FlowContext；其中resMap是从持久化层获取并转换为对应Invoker的输出；
```java
/**
* 从补偿信息处理返回值
*
* @param flowContext   上下文
* @param resMap        从补偿信息获取与Invoker定义的 results Map
*/
@SuppressWarnings("rawtypes")
void result4Compensate(FlowContext flowContext, HashMap resMap);
```
案例如下：
```java
@InvokerDefinition(
        name = "square invoker3",
        results = {
                IntValue3Key.class	//[1]
        }
)
public class SampleInvoker3 extends AbstractSkippableInvoker implements CheckPoint {
    @Override
    public void invoke(FlowContext ctx) {
        throw new ErrorMessageException(ErrorCode.SystemError, "SampleInvoker3 test for ErrorMessages");	//[2]
    }

    @Override
    public void result4Compensate(FlowContext flowContext, HashMap resMap) {	//[3]
        flowContext.put(IntValue3Key.class, (Integer)resMap.get(IntValue3Key.class.getName()));
    }
}
```
[1]：定义该Invoker的输出值；<br />[2]：根据业务逻辑主动抛出业务异常ErrorMessageException，供引擎统一处理中断逻辑；<br />[3]：实现从补偿信息提取[1]中定义的results，并根据业务逻辑加工后存入FlowContext；
<a name="kbwz2"></a>
## Outboundable
实现了Outboundable接口的Invoker需要实现多个方法：
```java
/**
 * 指定Invoker是否异步调用外部服务
 */
boolean isAsync();

 /**
 * 调用外部服务正常获取返回后的本地逻辑；
 * 实现类需要检查flowContext内已有SendOutResponseMsgKey(由invoke方法内的逻辑存入)；
 * 该方法的逻辑主要为后续逻辑及后续invoker传递根据外部服务返回加工后的数据，以及判定判定交易状态；
 * <p>
 *     注意：如果在旧版本中实现的Invoker未在该方法中实现返回值的处理，而是在invoke方法中直接处理的，就无法支持断点续交易的能力；
 * </p>
 *
 * {@link OutboundStatusBacking#judging(String)}
 *
 * @param flowContext   上下文
 */
void outboudingBacking(FlowContext flowContext);

/**
 * 有实现类装配调用外部逻辑需要的数据
 * @param flowContext 上下文
 */
void prepare2Customrized(FlowContext flowContext);
```
从AbstractFlow中看Outboundable-Invoker的整执行顺序大致如下：
> _执行顺序为：prepare->prepare2Customrized->必输字段检查->beforeOutboundGoing->invoke->afterOutboundBacking->outboudingBacking；<br />注意：如果isAsync为ture，则不会执行afterOutboundBacking->outboudingBacking，在invoke执行完后由引擎进行中断处理；_

isAsync：该方法如果返回true，说明实现该接口的Invoker在invoke方法的逻辑中会以异步的方式调用外部服务（丢给异步线程池，主线程不阻塞，主线程不关心调用外部服务的返回值）；那么此时引擎就会将当前Trans置为Break（在FlowContex中设置FinalResultKey为Break）来中断当前Trans；<br />prepare2Customrized：该方法的作用是在执行invoker方法前，由实现类装配调用外部服务时的出参（put到FlowContex内），引擎在调用invoke方法前会检查InvokerDefinition中配置的requires包含的值是否都不为空；<br />outboudingBacking：该方法用于实现调用外部服务正常获取返回后的本地逻辑；

- 在此方法中必须需要检查FlowContext内已存在SendOutResponseMsgKey(对于非Async的Invoker应该由invoke方法内的逻辑存入；而对于Async的Invoker则由引擎根据补偿交易持久化的返回值存入)；
- 另外此方法中必须存入OutTransStatusKey，该值用于引擎后续逻辑判定外调交易的最终状态，此值的判定逻辑必须通过实现OutboundStatusBacking接口的judging方法，该方法的入参即为SendOutResponseMsgKey；
<a name="YaoQN"></a>
### OutboundStatusBacking
该接口必须与Outboundable同时出现，其作用主要是为了统一根据外调服务的返回值判定Outboundable-Invoker的最终执行状态，供该Trans和对应的补偿Trans（CallBackOutboundFlow）分别调用；另外独立这个接口的目的是为了兼容老的版本；
```java
/**
* 根据Outbound的返回报文判定Outbound交易状态
*
* @param outboundResponse   返回报文
* @return OutTransStatusDef 交易状态
*/
TransStatusEnum judging(String outboundResponse);
```
judging：该方法用于根据outboundResponse判定交易状态，outboundResponse是外调服务返回报文（Map结构的Json字符串）；
<a name="r8cHR"></a>
### 案例
<a name="eurvU"></a>
#### 非异步Outboundable-Invoker
```java
@InvokerDefinition(
        name = "Outbound Sample Test Invoker ",
        requires = {
                //以下字段为必输字段
                //默认请求流水的id
                InboundIdKey.class,
                GlobleIdKey.class,
        },
        optional = {
                OutSignTokenKey.class,
                OutMqMsgIdKey.class,
                OutTransVersionKey.class,
                OutRequestUrlKey.class
        }

)
public class OutboundSampleInvoker extends AbstractSkippableInvoker implements Outboundable, OutboundStatusBacking {

    @Override
    public void invoke(FlowContext flowContext) {
        Map<String, String> sendMap = Maps.newHashMap();
        sendMap.put("name", "李四");
        //设置返回
        flowContext.put(SendOutResponseMsgKey.class, JSON.toJSONString(sendMap));	//[2]
    }

    @Override
    public boolean isAsync() {	//[2]
        return false;
    }

    @Override
    public void outboudingBacking(FlowContext ctx) {	//[3]
        String responseStr = ctx.get(SendOutResponseMsgKey.class);
        //根据response确定outbound的处理状态
        ctx.put(OutTransStatusKey.class, judging(responseStr));
    }

    @Override
    public void prepare2Customrized(FlowContext flowContext) {		//[1]
        flowContext.put(OutSvPrIdKey.class, "SCAC");
        Map<String, String> sendMap = Maps.newHashMap();
        sendMap.put("name", "张三");
        flowContext.put(OutRequestMsgKey.class, JSON.toJSONString(sendMap));

    }

    @Override
    public TransStatusEnum judging(String outboundResponse) {		
        if (ValidateUtilExt.isNotNullOrEmpty(outboundResponse)) {
            return TransStatusEnum.S;
        }
        return TransStatusEnum.F;
    }
}
```
[1]：为外调服务准备请求参数，存入OutRequestMsgKey；注意该方法会被Oubboundable接口的默认方法prepare调用，其内还会为外调服务准备内置的系统级参数，以及检查必输项，这些内置内置的系统级参数是否需要作为外调服务的请求参数，完全取决与应用逻辑，在invok方法中由应用自行处理，见上例中invok方法；prepare方法实现如下：
```java
default void prepare(FlowContext flowContext){
        flowContext.put(InboundIdKey.class, flowContext.get(TransIdKey.class));
        if (ValidateUtilExt.isNullOrEmpty(flowContext.get(GlobleIdKey.class))) {
            flowContext.put(GlobleIdKey.class, DbConstants.NULL);
        }
        flowContext.put(OutSignTokenKey.class, DbConstants.NULL);
        flowContext.put(OutMqMsgIdKey.class, DbConstants.NULL);
        flowContext.put(OutTransVersionKey.class, DbConstants.NULL);
        flowContext.put(OutRequestUrlKey.class, DbConstants.NULL);

        prepare2Customrized(flowContext);
        checkNotNull(flowContext.get(OutSvPrIdKey.class), "检查必输的上下文属性, OutSvPrIdKey不可为空");
        checkNotNull(flowContext.get(OutRequestMsgKey.class),"检查必输的上下文属性, OutRequestMsgKey不可为空");
    }
```

[2]：配置该Outboundable-Invoker是非异步的；那么其invoke方法中的逻辑就必须是阻塞获取外调服务返回的，因此在invoke方法中，必须存在存入SendOutResponseMsgKey的逻辑；<br />[3]：对外调服务的返回值进行处理，其中主要逻辑是：根据返回值，判定外调服务在业务逻辑上的执行状态（TransStatusEnum），因此需要调用统一的judging逻辑，此处提取该方法的目的也是为了在交易断续的补偿交易中可以使用相同的逻辑，避免业务逻辑的不一致性；
<a name="RjS9s"></a>
#### 异步Outboundable-Invoker
```java
@InvokerDefinition(
        name = "Outbound Sample2 Test Invoker ",
        requires = {
                //以下字段为必输字段
                //默认请求流水的id
                InboundIdKey.class,
                GlobleIdKey.class,
        },
        optional = {
                OutSignTokenKey.class,
                OutMqMsgIdKey.class,
                OutTransVersionKey.class,
                OutRequestUrlKey.class
        }

)
public class OutboundSample2Invoker extends AbstractSkippableInvoker implements Outboundable, OutboundStatusBacking {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(OutboundSample2Invoker.class);

    @Override
    public void invoke(FlowContext flowContext) {
        LOGGER.debug("调用outbound invoker");
    }

    @Override
    public boolean isAsync() {		//[2]
        return true;
    }

    @Override
    public void outboudingBacking(FlowContext ctx) {				//[3]
        String responseStr = ctx.get(SendOutResponseMsgKey.class);
        //根据response确定outbound的处理状态
        ctx.put(OutTransStatusKey.class, judging(responseStr));
    }

    @Override
    public void prepare2Customrized(FlowContext flowContext) {		//[1]
        flowContext.put(OutSvPrIdKey.class, "SCAC");
        Map<String, String> sendMap = Maps.newHashMap();
        sendMap.put("name", "张三");
        flowContext.put(OutRequestMsgKey.class, JSON.toJSONString(sendMap));

    }

    @Override
    public TransStatusEnum judging(String outboundResponse) {
        if (ValidateUtilExt.isNotNullOrEmpty(outboundResponse)) {
            return TransStatusEnum.S;
        }
        return TransStatusEnum.F;
    }
}
```
[1]：同上节<br />[2]：配置该Outboundable-Invoker是异步的；那么其invoke方法中的逻辑就必须是非阻塞的，外调服务直接丢给异步线程，因此在invoke方法中，不需要等待返回；引擎会主动在该Invoker位置中断执行，等待后续补偿交易（CallBackOutboundFlow）完成该Invoker的执行结果处理，以及等待该交易的幂等触发后续其他Invoker的继续执行；<br />[3]：此方法将在补偿交易（CallBackOutboundFlow）完成后，在交易幂等触发时调用；
<a name="IUa5h"></a>
## 补偿交易
补偿交易模式实际上是一种二阶段执行交易的设计模式，因此第一阶段产生的BoundId是连接两个阶段的重要桥梁；BoundId的产生由引擎负责，但应用系统需要确保BoundId被正确传递给第二阶段的发起者。
<a name="CIQe8"></a>
### CallBackOutboundFlow
专用于Outboundable-Invoker的，支持断续交易的补偿交易；
```java
@FlowDefinition(
        name = "交易结果回调",
        desc = "Outboundable Invoker的结果回调交易",
        invokers = {
                Callback4OutboundUpdateResult.class,
                TransactionSeperator.class,
                DetermineFinalResult.class,
        },
        response = {
                ErrorMessagesKey.class,
                FinalResultKey.class
        }
)
public class CallBackOutboundFlow extends AbstractFlow {
}

@InvokerDefinition(
        name = "Outbound交易回调更新",
        requires = {
                OutboundIdKey.class,
                SendOutResponseMsgKey.class
        }
)
public class Callback4OutboundUpdateResult implements Invoker {
    // ......
}
```
该交易主要的Invoker只有Callback4OutboundUpdateResult，其必须入参为：<br />OutboundIdKey：外调服务触发时记录Outbound流水的唯一ID；<br />SendOutResponseMsgKey：外调服务返回报文（Map结构的Json字符串）；
<a name="Yf4Oh"></a>
### CompensationResultFlow
专用于CheckPoint-Invoker的，支持断续交易的补偿交易；
```java
@FlowDefinition(
        name = "交易结果补偿",
        desc = "非Outboundable但实现CheckPoint接口的invoker的结果补偿交易",
        invokers = {
                CompensationResultSaving.class,
                TransactionSeperator.class,
                DetermineFinalResult.class,
        },
        response = {
                ErrorMessagesKey.class,
                FinalResultKey.class
        }
)
public class CompensationResultFlow extends AbstractFlow  {
}

@InvokerDefinition(
        name = "交易补偿结果保存",
        requires = {
                InboundIdKey.class,
                TransCodeKey.class,
                ResponseDataKey.class
        }
)
public class CompensationResultSaving implements Invoker {
    // ......
}
```
该交易主要的Invoker只有CompensationResultSaving，其必须入参为：

- InboundIdKey：标识Inbound流水的唯一ID；
- TransCodeKey：为要进行补偿处理的Invoker的全限定类名；
- ResponseDataKey：即要进行补偿处理的Invoker所需要传递到FlowContext，供后续Invoker使用的返回值（Map结构的Json字符串）；
<a name="HkL62"></a>
# 数据同步
第六步：当需要对 Inbound 和 Outbound 流水数据进行数据同步时，可以打开系统开关，目前只支持复制到Elasticsearch；同步的过程是采用异步事件驱动复制的方式，不会对交易执行的主体线程产生影响；配置如下：
```yaml
# [1]
spring:
  data:
    elasticsearch:
      repositories:
        enabled: true
  elasticsearch:
    rest:
      uris: ubuntu.wsl:19200
      connection-timeout: 10s
      read-timeout: 30s
      password:
      username:

# [2]
pg:
  trans-flow:
    datasync:
      enabled: true
  storage:
    datasync:
      batch-size: 10
      flush-interval: 5
      ring-buffer-size: 1024
```
[1]：Elasticsearch相关配置，主要是需要激活spring-data-elasticsearch-repositories组件，和Elasticsearch服务器的相关配置；<br />[2]：激活数据同步组件，以及存储层相关配置，其中batch-size和flush-interval只要一个条件达成就会触发数据同步逻辑：

- batch-size:  批量提交到存储层的事件量，默认100；
- flush-interval:  每间隔多少秒，执行一次批量提交，默认5秒；
- ring-buffer-size:  Disruptor的Ring Buffer Size，必须是2的次幂，默认1024；

