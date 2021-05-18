package net.engining.zeebe.spring.client.ext.autotest.cases;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.ZeebeClientLifecycle;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import net.engining.zeebe.spring.client.ext.ZeebeWorkerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-04-16 16:40
 * @since :
 **/
@Service
public class DemoProcessFooWorkerService implements ZeebeWorkerHandler<Map<String, Object>> {

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
    @ZeebeWorker(type = TYPE_ID, name = DemoProcessStarterService.PROCESS_WORKER)
    public void handleFooJob(final JobClient client, final ActivatedJob job){
        Map<String, Object> map = new HashMap<String, Object>();
        defaultWork(
                client,
                job,
                map,
                null,
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
    public void before(Map<String, Object> event) {
        //do nothing
    }

    @Override
    public void after(Map<String, Object> event, boolean rt) {
        //do nothing
    }
}
