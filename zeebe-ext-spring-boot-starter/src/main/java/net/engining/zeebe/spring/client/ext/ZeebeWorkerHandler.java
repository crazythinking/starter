package net.engining.zeebe.spring.client.ext;

import io.camunda.zeebe.client.api.ZeebeFuture;
import io.camunda.zeebe.client.api.command.CompleteJobCommandStep1;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.CompleteJobResponse;
import io.camunda.zeebe.client.api.worker.JobClient;
import net.engining.pg.support.utils.ExceptionUtilsExt;
import net.engining.pg.support.utils.ValidateUtilExt;
import net.engining.zeebe.spring.client.ext.bean.DefaultRequestHeader;
import net.engining.zeebe.spring.client.ext.bean.ZeebeContext;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * Zeebe 流程节点执行处理器接口
 *
 * @author : Eric Lu
 * @date : 2021-04-25 17:43
 **/
public interface ZeebeWorkerHandler<E, R> extends Handler<ZeebeContext<DefaultRequestHeader, E>, R> {

    /**
     * Zeebe worker 的执行，发送指令到zeebe broker，推动相应service node 执行
     * @param client        JobClient
     * @param job           ActivatedJob
     * @param event         发送到zeebe broker的variables
     * @param returnTimeout 获取返回最大等待时间
     */
    default void defaultWork(JobClient client, ActivatedJob job,
                             ZeebeContext<DefaultRequestHeader, E> event, Integer returnTimeout
    ){

        before(event, Type.WORKER, getLogger());

        R response = beforeHandler(event);
        event.getNodeContext().put(getTypeId(), response);

        ZeebeFuture<CompleteJobResponse> zeebeFuture;

        try {
            CompleteJobCommandStep1 step1 = client.newCompleteCommand(job.getKey()).variables(event);

            zeebeFuture = step1.send();

            if (ValidateUtilExt.isNotNullOrEmpty(returnTimeout)){
                zeebeFuture.join(returnTimeout, TimeUnit.SECONDS);
            }
            else {
                zeebeFuture.join();
            }

            logJob(job);
            after(event, true, Type.WORKER, getLogger());
        }
        catch (Exception e){
            after(event, false, Type.WORKER, getLogger());
            ExceptionUtilsExt.dump(e);
        }

    }

    /**
     * 日志
     * @param job   ActivatedJob
     */
    default void logJob(ActivatedJob job){
        getLogger().debug(
                "complete Zeebe handler [type: {}, key: {}, element: {}, workflow instance: {}] " +
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
