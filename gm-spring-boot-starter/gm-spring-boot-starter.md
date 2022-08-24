该组件用于激活PaaS平台的通用配置项；需要配合相关依赖及激活开关才能生效；
# GeneralAutoConfiguration
默认开启GeneralAutoConfiguration；将向容器注入：

   - GmCommonProperties：PaaS平台应用的基础配置项
   - ApplicationContextHolder：当前Spring ApplicationContext的快速访问工具，提供静态的快捷方法访问Bean；
   - Provider4Organization：机构信息的提供者，根据GmCommonProperties中的配置，实例化进程安全或线程安全的机构信息；
   - Log4jMarkerService；基于自定义标识的，日志级别的存储服务；注意因为基于Log4j的，极端情况下存在丢失数据的风险，不可用于强一致性场景；
# AsyncAutoConfiguration
通过配置`gm.config.enabled.async=true`激活，开启AsyncAutoConfiguration；用于Spring异步方法调用的场景，将向容器注入全局的异步线程池；
# Oauth2ExtAutoConfiguration
通过配置`gm.config.enabled.oauth2=true`激活，开启Oauth2ExtAutoConfiguration；用于作为OAuth Resource Server资源服务的场景，需要引入如下依赖：
```xml
<!-- spring security, oauth2依赖 -->
<dependency>
  <groupId>org.springframework.security.oauth.boot</groupId>
  <artifactId>spring-security-oauth2-autoconfigure</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-oauth2</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- redis，用于存储Token -->
<dependency>
  <groupId>net.engining.starter</groupId>
  <artifactId>redis-spring-boot-starter</artifactId>
</dependency>

```
# ParameterAutoConfiguration
通过配置`gm.config.enabled.parameter=true`激活，开启ParameterAutoConfiguration；用于参数体系支持，需要引入如下依赖：
```xml
<dependency>
  <groupId>net.engining.pg</groupId>
  <artifactId>pg-parameter-help</artifactId>
</dependency>

<!-- 如果参数需要分布式缓存支持，则添加redis依赖 -->
<dependency>
  <groupId>net.engining.starter</groupId>
  <artifactId>redis-spring-boot-starter</artifactId>
</dependency>
```
将向容器注入：

- PgParamAndCacheProperties：参数体系相关配置项
- ParameterFacility：参数体系的操作服务
# RetryAutoConfiguration
通过配置`gm.config.enabled.retry=true`激活，开启RetryAutoConfiguration；基于Spring Retry的重试，需要引入如下依赖：
```xml
<!-- 重试支持 -->
<dependency>
  <groupId>org.springframework.retry</groupId>
  <artifactId>spring-retry</artifactId>
</dependency>
```
# SchedulingAutoConfiguration
通过配置`gm.config.enabled.scheduling=true`激活，开启SchedulingAutoConfiguration；用于Spring Schedule的定时任务；
# SnowflakeSequenceAutoConfiguration
通过配置`gm.config.enabled.snowflake-sequence=true`激活，开启SnowflakeSequenceAutoConfiguration；用于需要使用雪花算法作为分布式序列号的场景；
# SwaggerAutoConfiguration
通过配置`gm.config.enabled.swagger=true`激活，开启SwaggerAutoConfiguration；用于需要使用Swagger的场景，需要引入如下依赖：
```xml
<!-- swagger -->
<dependency>
  <groupId>io.springfox</groupId>
  <artifactId>springfox-swagger2</artifactId>
</dependency>
<dependency>
  <groupId>io.springfox</groupId>
  <artifactId>springfox-swagger-ui</artifactId>
</dependency>
```
# WebMvcAutoConfiguration
通过配置`gm.config.enabled.webmvc=true`激活，开启WebMvcAutoConfiguration；注入基于Spring WebMVC服务的通用配置；需要引入如下依赖：
```xml
<dependency>
  <groupId>net.engining.pg</groupId>
  <artifactId>pg-web</artifactId>
</dependency>

<dependency>
  <groupId>commons-fileupload</groupId>
  <artifactId>commons-fileupload</artifactId>
</dependency>
```
将向容器注入：

- GlobalControllerExceptionHandler：统一异常处理
- Slf4jMappedDiagnosticContextFilter：日志过滤器
- CommonsMultipartResolver：文件下载服务
# WebSecurityAutoConfiguration
通过配置`gm.config.enabled.security=true`激活，开启WebSecurityAutoConfiguration；自动注入基于Spring Security 的 Actuator安全配置；需要引入如下依赖：
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```
