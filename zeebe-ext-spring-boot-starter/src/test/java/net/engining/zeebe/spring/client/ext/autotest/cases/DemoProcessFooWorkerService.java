package net.engining.zeebe.spring.client.ext.autotest.cases;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.ZeebeClientLifecycle;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import net.engining.zeebe.spring.client.ext.ZeebeWorkerHandler;
import net.engining.zeebe.spring.client.ext.bean.ZeebeContext;
import net.engining.zeebe.spring.client.ext.bean.ZeebeResponse;
import net.engining.zeebe.spring.client.ext.bean.DefaultRequestHeader;
import net.engining.zeebe.spring.client.ext.bean.DefaultResponseHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-04-16 16:40
 * @since :
 **/
@Service
public class DemoProcessFooWorkerService implements ZeebeWorkerHandler<Foo, Foo2> {

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DemoProcessFooWorkerService.class);
    public static final String TYPE_ID = "foo";

    @Autowired
    private ZeebeClientLifecycle client;

    /**
     * 当Event到达时由Zeebe自身调用
     */
    @ZeebeWorker(
            type = TYPE_ID,
            name = StandardizingDemoProcessStarterService.PROCESS_WORKER,
            timeout = -1,
            requestTimeout = -1,
            maxJobsActive = -1,
            pollInterval = -1
    )
    public void handleJob(final JobClient client, final ActivatedJob job){
        //获取当前流程的 context variables
        ZeebeContext<DefaultRequestHeader, Foo> event = JSON.parseObject(
                job.getVariables(),
                new TypeReference<ZeebeContext<DefaultRequestHeader, Foo>>(){}
        );

        job.getElementInstanceKey();
        defaultWork(
                client,
                job,
                event,
                null
        );

    }

    @Override
    public ZeebeClientLifecycle getZeebeClientLifecycle() {
        return client;
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public String getTypeId() {
        return TYPE_ID;
    }

    @Override
    public Foo2 beforeHandler(
            ZeebeContext<DefaultRequestHeader, Foo> event
    ) {
        //do biz-logic before sending event to zeebe broker
        Foo2 foo2 = new Foo2();
        foo2.setF1(event.getRequestData().getF1());
        foo2.setF2(event.getRequestData().getF2().multiply(BigDecimal.valueOf(10)).setScale(2, RoundingMode.DOWN));
        foo2.setUid(UUID.fastUUID().toString());
        return foo2;
    }

    @Override
    public void afterHandler(ZeebeContext<DefaultRequestHeader, Foo> event, boolean rt) {
        //do biz-logic after sent event to zeebe broker
    }
}
