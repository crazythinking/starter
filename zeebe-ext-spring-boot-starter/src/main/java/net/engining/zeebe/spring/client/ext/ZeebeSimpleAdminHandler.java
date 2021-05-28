package net.engining.zeebe.spring.client.ext;

import cn.hutool.core.util.StrUtil;
import io.camunda.zeebe.client.api.command.DeployProcessCommandStep1;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.client.api.response.Topology;
import io.camunda.zeebe.spring.client.ZeebeClientLifecycle;
import net.engining.pg.support.utils.ExceptionUtilsExt;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Zeebe 流程管理处理器接口
 *
 * @author Eric Lu
 * @date 2021-05-13 9:56
 **/
public class ZeebeSimpleAdminHandler implements Handler<String, Void> {

    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(ZeebeSimpleAdminHandler.class);
    public static final String ADMIN_ID = "SimpleAdmin";

    private ZeebeClientLifecycle client;

    @Autowired
    public void setClient(ZeebeClientLifecycle client) {
        this.client = client;
    }

    /**
     * 发布流程文档
     *
     * @param filename  流程文档的绝对路径(例如“〜/wf/workflow.bpmn”)
     * @return {@link DeploymentEvent} 发布结果
     */
    public Optional<DeploymentEvent> deploy(@NotNull String filename){
        if (!getZeebeClientLifecycle().isRunning()) {
            getLogger().warn("Zeebe Client is not running, cannot do anything!!!");
            return Optional.empty();
        }

        String event = "Deploy-" + StringUtils.substringAfterLast(filename, StrUtil.SLASH);
        before(event, Type.ADMIN, getLogger());

        try {
            DeployProcessCommandStep1 deployProcessCommandStep1 = getZeebeClientLifecycle().newDeployCommand();
            DeploymentEvent deploymentResult = deployProcessCommandStep1.addResourceFile(filename).send().join();

            getLogger().info(
                    "Deployed: {}",
                    deploymentResult
                            .getProcesses()
                            .stream()
                            .map(wf -> String.format("<%s:%d>", wf.getBpmnProcessId(), wf.getVersion()))
                            .collect(Collectors.joining(",")));

            after(event, true, Type.ADMIN, getLogger());
            return Optional.of(deploymentResult);
        }
        catch (Exception e) {
            after(event, false, Type.ADMIN, getLogger());
            ExceptionUtilsExt.dump(e);
            return Optional.empty();
        }

    }

    /**
     * 获取当前拓扑结构
     *
     * @return  {@link Topology} 拓扑结构对象
     */
    public Optional<Topology> getTopology(){
        if (!getZeebeClientLifecycle().isRunning()) {
            getLogger().warn("Zeebe Client is not running, cannot do anything!!!");
            return Optional.empty();
        }

        String event = "Topology";
        before(event, Type.ADMIN, getLogger());

        try {
            Topology topology = getZeebeClientLifecycle().newTopologyRequest().send().join();

            StringBuilder stringBuilder = new StringBuilder();
            topology.getBrokers()
                    .forEach(
                            b -> {
                                stringBuilder.append(StringUtils.repeat(StringUtils.SPACE, 4));
                                stringBuilder.append(b.getAddress());
                                stringBuilder.append(StringUtils.LF);
                                b.getPartitions().forEach(
                                        p -> {
                                            stringBuilder.append(StringUtils.repeat(StringUtils.SPACE, 6));
                                            stringBuilder.append(p.getPartitionId());
                                            stringBuilder.append(" - ");
                                            stringBuilder.append(p.getRole());
                                            stringBuilder.append(StringUtils.LF);
                                        });
                            });
            getLogger().info("Current Brokers status:" + StringUtils.LF + stringBuilder);

            after(event, true, Type.STARTER, getLogger());
            return Optional.of(topology);
        }
        catch (Exception e) {
            after(event, false, Type.ADMIN, getLogger());
            ExceptionUtilsExt.dump(e);
            return Optional.empty();
        }

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
        return ADMIN_ID;
    }

    @Override
    public Void beforeHandler(String event) {
        //do nothing
        return null;
    }

    @Override
    public void afterHandler(String event, boolean rt) {
        //do nothing
    }


}
