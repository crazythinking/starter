package net.engining.zeebe.spring.client.ext;

import io.camunda.zeebe.client.api.ZeebeFuture;
import io.camunda.zeebe.client.api.command.CompleteJobCommandStep1;
import io.camunda.zeebe.client.api.command.FinalCommandStep;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.CompleteJobResponse;
import io.camunda.zeebe.client.api.worker.JobClient;
import net.engining.pg.support.utils.ValidateUtilExt;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * Zeebe 流程节点执行处理器接口
 *
 * @author : Eric Lu
 * @date : 2021-04-25 17:43
 **/
public interface ZeebeWorkerHandler<E> extends Handler<E>{

    default void defaultWork(JobClient client, ActivatedJob job, E event,
                             Integer requestTimeout, Integer returnTimeout){

        defalutBefore(event, Type.WORKER, getLogger());

        FinalCommandStep<CompleteJobResponse> finalCommandStep;
        ZeebeFuture<CompleteJobResponse> zeebeFuture;

        try {
            CompleteJobCommandStep1 step1 = client
                    .newCompleteCommand(job.getKey())
                    .variables(event);

            if (ValidateUtilExt.isNullOrEmpty(requestTimeout)) {
                zeebeFuture = step1.send();
            } else {
                finalCommandStep = step1.requestTimeout(Duration.ofSeconds(requestTimeout));
                zeebeFuture = finalCommandStep.send();
            }

            if (ValidateUtilExt.isNotNullOrEmpty(returnTimeout)){
                zeebeFuture.join(requestTimeout, TimeUnit.SECONDS);
            }
            else {
                zeebeFuture.join();
            }

            logJob(job);
            defaultAfter(event, true, Type.WORKER, getLogger());
        }
        catch (Exception e){
            defaultAfter(event, false, Type.WORKER, getLogger());
            throw e;
        }

    }

    default void logJob(ActivatedJob job){
        getLogger().debug(
                "complete job [type: {}, key: {}, element: {}, workflow instance: {}] " +
                        "[deadline: {}] " +
                        "[headers: {}] " +
                        "[variables: {}]",
                job.getType(),
                job.getKey(),
                job.getElementId(),
                job.getProcessInstanceKey(),
                Instant.ofEpochMilli(job.getDeadline()),
                job.getCustomHeaders(),
                job.getVariables());
    }

}
