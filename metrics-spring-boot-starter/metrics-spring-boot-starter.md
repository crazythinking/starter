该组件基于Spring Boot Actuator 和 io.micrometer，为微服务的监控指标提供支撑，除SpringBoot自带的监控指标，还增加了Sentinel和Undertow相关监控指标；同时支持自动识别符合JMX规范的监控指标；以及为应用服务层自定义的监控指标提供方便的接入方法；以上各类监控指标均会统一整合为Prometheus Export的格式吐出，以便于接入统一监控体系；
# 使用说明
第一步：starter依赖
```xml
<dependency>
  <groupId>net.engining.starter</groupId>
  <artifactId>metrics-spring-boot-starter</artifactId>
  <version>${starter.version}</version>
</dependency>
```
## 自定义指标
第二步：定义指标；在实现自定义指标前，需要先了解指标的命名规范，由于Spring Boot Actuator本身也是基于 io.micrometer体系的，因此自定义指标同样遵循 io.micrometer的命名规范：以Tag作为维度的命名体系；Tag(标签) 是 Micrometer 的一个重要的功能，严格来说，一个度量框架只有实现了标签的功能，才能真正地多维度进行度量数据收集。Tag 的命名一般需要是有意义的，所谓有意义就是可以根据 Tag 的命名可以推断出它指向的数据到底代表什么维度或者什么类型的度量指标。如下例子，假设我们需要监控数据库的调用和 Http 请求调用统计，一般推荐的做法是：
```java
MeterRegistry registry = ...
registry.counter("database.calls", "db", "users")
registry.counter("http.requests", "uri", "/api/users")
```
这样，当我们选择命名为”database.calls” 的计数器，我们可以进一步选择分组”db” 或者”users” 分别统计不同分组对总调用数的贡献或者组成。
在统一静态类中定义所有自定义指标，如下案例中，要分别统计所有业务的MVC服务指定窗口内的调用次数，以及从一开始累计的调用总次数：
```java
public class BizMetrics {
    private static final String BIZ_MVC = "biz.mvc.";
    public static final String BIZ_MVC_CALL = BIZ_MVC + "call";

    public static Counter.Builder requestStepTimes(String uri) {
        return Counter.builder(BIZ_MVC_CALL)
                .tags(
                        "uri", uri,
                        "type", "step"
                )
                ;
    }

    public static Counter.Builder requestTotalTimes(String uri) {
        return Counter.builder(BIZ_MVC_CALL)
                .tags(
                        "uri", uri,
                        "type", "cumulative"
                )
                ;
    }
}
```
第二步：实现自定义指标的存储层，该步骤是非必须，若未定义则默认的自定义指标存储层为SimpleMetricsRepositoriesServiceImpl，该实现只产生Trace级日志；
```java
public class MyMetricsRepositoriesServiceImpl extends SimpleMetricsRepositoriesServiceImpl {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(MyMetricsRepositoriesServiceImpl.class);

    @Override
    public void batchSave(List<?> entities) {
        entities.forEach(o -> {
            if (o instanceof MeterDto){
                MeterDto meter = (MeterDto) o;
                //save to storage
            }
        });
    }

    @Override
    public void initializeMeters(MeterRegistry meterRegistry) {
        //load from storage
        double call1 = 20;
        double call2 = 20;

        //用存储获取的记录值初始化指标值
        BizMetrics.requestTotalTimes("/mvcecho/111").register(meterRegistry).increment(call1);
        BizMetrics.requestTotalTimes("/mvcecho3").register(meterRegistry).increment(call2);

    }
}
```
第三步：注册指标；针对自定义指标，本组件扩展了基于数据存储层的指标注册器，可将监控指标数据发布到自定义的数据存储层，其分为两类：

- StoredPushMeterRegistry：_该注册器内包含的指标都是累计值，即从初始化那一刻开始一直累计，因此注意重启服务时需要从存储层初始化各指标；_
- StoredStepMeterRegistry：_该注册器内包含的指标都是按指定步长的累计值；_

首先根据需要分别指定两个注册器需要存储的指标，通过指标命名的前缀进行过滤（通常在BizMetrics中定义），若不配置，将不会对任何指标数据进行存储；
```
//对应StoredStepMeterRegistry，多个前缀可逗号分隔
pg.metrics.registry.step-meter-registry-prefixes=biz.mvc
//同时控制其指标计算的间隔周期与数据发布的间隔周期；
pg.metrics.registry.step-meter-registry-interval=10

//对应StoredPushMeterRegistry，多个前缀可逗号分隔
pg.metrics.registry.push-meter-registry-prefixes=biz.mvc
//只控制其指标数据发布的间隔周期；
pg.metrics.registry.push-meter-registry-interval=10
```
然后在服务初始化阶段将指标注册到对应的注册器中；
```java
private Counter counter1;
private Counter counter2;
private Counter counter3;
private Counter counter4;

counter1 = BizMetrics.requestStepTimes("/mvcecho/111").register(storedStepMeterRegistry);
counter2 = BizMetrics.requestStepTimes("/mvcecho3").register(storedStepMeterRegistry);

counter3 = BizMetrics.requestTotalTimes("/mvcecho/111").register(storedPushMeterRegistry);
counter4 = BizMetrics.requestTotalTimes("/mvcecho3").register(storedPushMeterRegistry);
```
第四步：触发指标器；在业务逻辑的适当位置调用指标器；
```java
private void call2(AdditionalOb ob) throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .post("/mvcecho3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(ob))
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
        ;
        
        //触发指标器
        counter2.increment();
        counter4.increment();
    }

    private AdditionalOb call1() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .get("/mvcecho/111")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk());

        String json = "{\"aa\":\"bbb\"}";
        AdditionalOb ob = mapper.readValue(json, AdditionalOb.class);

        //触发指标器
        counter1.increment();
        counter3.increment();

        return ob;
    }
```
## 其他默认加载的指标
Undertow：当ClassPath中存在Undertow时，默认自动注册并激活相应的监控指标；
Sentinel：当ClassPath中存在MetricExtension时，默认自动注册并激活相应的监控指标；
可通过以下配置关闭：
```
pg.metrics.undertow.enabled=false
pg.metrics.sentinel.enabled=false
```
## 整合Dropwizard Metrics体系的指标
由于某些组件默认采用了Dropwizard Metrics体系，因此需要将其统一整合到io.micrometer体系下；
# Export Prometheus
以上提到的所有指标，默认情况下都会被导出到Actuator 的 Prometheus Endpoint，供Prometheus抓取；可通过以下配置关闭：
```
management.metrics.export.prometheus.enabled=false
```
# 案例代码
下载获取：metrics-spring-boot-starter-{starter.version}-test-sources.jar，详见如下案例：

- WebMvcTest.java
