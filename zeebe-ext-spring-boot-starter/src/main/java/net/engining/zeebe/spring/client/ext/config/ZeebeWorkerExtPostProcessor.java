package net.engining.zeebe.spring.client.ext.config;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.worker.JobWorkerBuilderStep1;
import io.camunda.zeebe.spring.client.bean.ClassInfo;
import io.camunda.zeebe.spring.client.bean.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.bean.value.factory.ReadZeebeWorkerValue;
import io.camunda.zeebe.spring.client.config.processor.ZeebeWorkerPostProcessor;
import net.engining.pg.support.utils.ValidateUtilExt;
import net.engining.zeebe.spring.client.ext.prop.ZeebeExtProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.springframework.util.ReflectionUtils.doWithMethods;

/**
 * 由于注解{@link io.camunda.zeebe.spring.client.annotation.ZeebeWorker}不能动态的获取配置，因此利用重载 Post Porcesser 进行扩展，
 * 且以外部化配置项的优先级为高；
 *
 * @author : Eric Lu
 * @version :
 * @date : 2021-05-31 13:36
 * @since :
 **/
public class ZeebeWorkerExtPostProcessor extends ZeebeWorkerPostProcessor {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(ZeebeWorkerExtPostProcessor.class);

    private final ReadZeebeWorkerValue reader;

    private ZeebeExtProperties zeebeExtProperties;

    public ZeebeWorkerExtPostProcessor(ReadZeebeWorkerValue reader, ZeebeExtProperties zeebeExtProperties) {
        super(reader);
        this.reader = reader;
        this.zeebeExtProperties = zeebeExtProperties;
    }

    @Override
    public Consumer<ZeebeClient> apply(final ClassInfo beanInfo) {
        LOGGER.info("zeebeWorker: {}", beanInfo);

        final List<ZeebeWorkerValue> annotatedMethods = new ArrayList<>();

        doWithMethods(
                beanInfo.getTargetClass(),
                method -> reader.apply(beanInfo.toMethodInfo(method)).ifPresent(annotatedMethods::add),
                ReflectionUtils.USER_DECLARED_METHODS);

        return client ->
                annotatedMethods.forEach(
                        m -> {
                            final JobWorkerBuilderStep1.JobWorkerBuilderStep3 builder = client
                                    .newWorker()
                                    .jobType(m.getType())
                                    .handler((jobClient, job) -> m.getBeanInfo().invoke(jobClient, job));

                            // using defaults from config if null, 0 or negative
                            if (m.getName() != null && m.getName().length() > 0) {
                                builder.name(m.getName());
                            }
                            if (m.getMaxJobsActive() > 0) {
                                builder.maxJobsActive(m.getMaxJobsActive());
                            }
                            if (m.getTimeout() > 0) {
                                builder.timeout(m.getTimeout());
                            }
                            if (m.getPollInterval() > 0) {
                                builder.pollInterval(Duration.ofMillis(m.getPollInterval()));
                            }
                            //如果扩展的配置项已配置则取之
                            if (ValidateUtilExt.isNotNullOrEmpty(zeebeExtProperties.getWorkerEntities().get(m.getType()))){
                                builder.requestTimeout(
                                        Duration.ofSeconds(
                                                zeebeExtProperties.getWorkerEntities().get(m.getType()).getReturnTimeout()
                                        )
                                );
                            }
                            else {
                                if (m.getRequestTimeout() > 0) {
                                    builder.requestTimeout(Duration.ofSeconds(m.getRequestTimeout()));
                                }
                            }
                            if (m.getFetchVariables().length > 0) {
                                builder.fetchVariables(m.getFetchVariables());
                            }

                            builder.open();

                            LOGGER.info("register job worker: {}", m);
                        });
    }
}
