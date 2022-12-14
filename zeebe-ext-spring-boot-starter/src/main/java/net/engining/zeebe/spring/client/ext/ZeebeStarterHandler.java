package net.engining.zeebe.spring.client.ext;

import io.camunda.zeebe.client.api.ZeebeFuture;
import io.camunda.zeebe.client.api.command.CreateProcessInstanceCommandStep1;
import io.camunda.zeebe.client.api.command.FinalCommandStep;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.response.ProcessInstanceResult;
import net.engining.pg.support.utils.ExceptionUtilsExt;
import net.engining.pg.support.utils.ValidateUtilExt;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Zeebe 流程启动处理器接口
 *
 * @author : Eric Lu
 * @date : 2021-04-25 17:43
 **/
public interface ZeebeStarterHandler<E> extends Handler<E,Void>{

    /**
     * 启动流程并直接返回
     * @param processId         流程命名标识
     * @param event             流程启动事件对象
     * @param version           流程版本
     * @param requestTimeout    请求启动超时秒数
     * @param returnTimeout     返回结果超时秒数
     * @return Optional<ProcessInstanceEvent> 可为空
     */
    default Optional<ProcessInstanceEvent> defaultStart(@NotNull String processId, @NotNull E event,
                                                        Integer version, Long requestTimeout, Long returnTimeout){
        if (!getZeebeClientLifecycle().isRunning()) {
            getLogger().warn("Zeebe Client is not running, cannot do anything!!!");
            return Optional.empty();
        }

        before(event, Type.STARTER, getLogger());

        try {
            CreateProcessInstanceCommandStep1.CreateProcessInstanceCommandStep2 step2 =
                    getZeebeClientLifecycle().newCreateInstanceCommand().bpmnProcessId(processId);
            CreateProcessInstanceCommandStep1.CreateProcessInstanceCommandStep3 step3;
            FinalCommandStep<ProcessInstanceEvent> finalCommandStep;
            ZeebeFuture<ProcessInstanceEvent> zeebeFuture;
            ProcessInstanceEvent processInstanceEvent;

            if (ValidateUtilExt.isNotNullOrEmpty(version)){
                step3 = step2.version(version);
            }
            else {
                step3 = step2.latestVersion();
            }
            step3 = step3.variables(event);

            if (ValidateUtilExt.isNotNullOrEmpty(requestTimeout)){
                finalCommandStep = step3.requestTimeout(Duration.ofSeconds(requestTimeout));
                zeebeFuture = finalCommandStep.send();
            }
            else {
                zeebeFuture = step3.send();
            }

            if (ValidateUtilExt.isNotNullOrEmpty(returnTimeout)){
                processInstanceEvent = zeebeFuture.join(requestTimeout, TimeUnit.SECONDS);
            }
            else {
                processInstanceEvent = zeebeFuture.join();
            }

            getLogger().debug(
                    "started instance:{} for ProcessDefinitionKey={}, bpmnProcessId={}, version={}",
                    processInstanceEvent.getProcessInstanceKey(),
                    processInstanceEvent.getProcessDefinitionKey(),
                    processInstanceEvent.getBpmnProcessId(),
                    processInstanceEvent.getVersion()
            );

            after(event, true, Type.STARTER, getLogger());
            return Optional.of(processInstanceEvent);
        }
        catch (Exception e){
            after(event, false, Type.STARTER, getLogger());
            ExceptionUtilsExt.dump(e);
            return Optional.empty();
        }

    }

    /**
     * 启动流程并阻塞当前线程，以等待流程执行结束返回结果
     *
     * @param processId         流程命名标识
     * @param event             流程启动事件对象
     * @param version           流程版本
     * @param requestTimeout    请求启动超时秒数
     * @param returnTimeout     返回结果超时秒数
     * @return Optional<ProcessInstanceResult> 可为空
     */
    default Optional<ProcessInstanceResult> defaultStart4Results(@NotNull String processId, @NotNull E event,
                                                                 Integer version, Long requestTimeout, Long returnTimeout){
        if (!getZeebeClientLifecycle().isRunning()) {
            getLogger().warn("Zeebe Client is not running, cannot do anything!!!");
            return Optional.empty();
        }

        before(event, Type.STARTER, getLogger());

        try {
            CreateProcessInstanceCommandStep1.CreateProcessInstanceWithResultCommandStep1 step1;
            CreateProcessInstanceCommandStep1.CreateProcessInstanceCommandStep2 step2 =
                    getZeebeClientLifecycle().newCreateInstanceCommand().bpmnProcessId(processId);
            CreateProcessInstanceCommandStep1.CreateProcessInstanceCommandStep3 step3;
            FinalCommandStep<ProcessInstanceResult> finalCommandStepRet;
            ZeebeFuture<ProcessInstanceResult> zeebeFutureRet;
            ProcessInstanceResult processInstanceRet;

            if (ValidateUtilExt.isNotNullOrEmpty(version)){
                step3 = step2.version(version);
            }
            else {
                step3 = step2.latestVersion();
            }
            step3 = step3.variables(event);
            step1 = step3.withResult();
            if (ValidateUtilExt.isNotNullOrEmpty(requestTimeout)){
                finalCommandStepRet = step1.requestTimeout(Duration.ofSeconds(requestTimeout));
                zeebeFutureRet = finalCommandStepRet.send();
            }
            else {
                zeebeFutureRet = step1.send();
            }

            if (ValidateUtilExt.isNullOrEmpty(returnTimeout)){
                processInstanceRet = zeebeFutureRet.join();
            }
            else {
                processInstanceRet = zeebeFutureRet.join(requestTimeout, TimeUnit.SECONDS);
            }

            getLogger().debug(
                    "started instance:{} for ProcessDefinitionKey={}, bpmnProcessId={}, version={}, results={}",
                    processInstanceRet.getProcessInstanceKey(),
                    processInstanceRet.getProcessDefinitionKey(),
                    processInstanceRet.getBpmnProcessId(),
                    processInstanceRet.getVersion(),
                    processInstanceRet.getVariables()
            );

            after(event, true, Type.STARTER, getLogger());
            return Optional.of(processInstanceRet);
        }
        catch (Exception e){
            after(event, false, Type.STARTER, getLogger());
            ExceptionUtilsExt.dump(e);
            return Optional.empty();
        }

    }

}
