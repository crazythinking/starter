package net.engining.zeebe.spring.client.ext.autotest.cases;

import io.camunda.zeebe.spring.client.ZeebeClientLifecycle;
import net.engining.pg.support.utils.ValidateUtilExt;
import net.engining.zeebe.spring.client.ext.AbstractZeebeStarterHandler;
import net.engining.zeebe.spring.client.ext.bean.ZeebeContext;
import net.engining.zeebe.spring.client.ext.bean.ZeebeResponse;
import net.engining.zeebe.spring.client.ext.bean.DefaultRequestHeader;
import net.engining.zeebe.spring.client.ext.bean.DefaultResponseHeader;
import net.engining.zeebe.spring.client.ext.prop.ZeebeExtProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author : Eric Lu
 * @date : 2021-05-13 15:15
 **/
@Service
public class StandardizingDemoProcessStarterService extends AbstractZeebeStarterHandler<Foo> {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(StandardizingDemoProcessStarterService.class);
    public static final String PROCESS_ID = "demoProcess";
    public static final String PROCESS_WORKER = "demoProcess-worker";
    public static final String PROCESS_STARTER= "demoProcess-starter";

    @Autowired
    private ZeebeExtProperties zeebeExtProperties;

    @Autowired
    private ZeebeClientLifecycle client;

    public ZeebeResponse<DefaultResponseHeader, Map<String, Object>> startProcessUntilCompletion(
            ZeebeContext<DefaultRequestHeader, Foo> request, Integer version
    ) {
        Long returnTimeout = null;
        Long requestTimeout = null;
        if (ValidateUtilExt.isNotNullOrEmpty(zeebeExtProperties.getStarterEntities().get(PROCESS_STARTER))){
            returnTimeout = zeebeExtProperties.getStarterEntities().get(PROCESS_STARTER).getReturnTimeout();
            requestTimeout = zeebeExtProperties.getStarterEntities().get(PROCESS_STARTER).getRequestTimeout();
        }
        return start4Results(
                PROCESS_ID,
                request,
                version,
                requestTimeout,
                returnTimeout
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
        return PROCESS_ID;
    }

    @Override
    public Void beforeHandler(ZeebeContext<DefaultRequestHeader, Foo> event) {
        //do nothing
        return null;
    }

    @Override
    public void afterHandler(ZeebeContext<DefaultRequestHeader, Foo> event, boolean rt) {
        //do nothing
    }

}
