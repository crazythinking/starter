package net.engining.zeebe.spring.client.ext;

import com.google.common.collect.Maps;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.response.ProcessInstanceResult;
import net.engining.pg.support.core.exception.ErrorCode;
import net.engining.zeebe.spring.client.ext.bean.ZeebeContext;
import net.engining.zeebe.spring.client.ext.bean.ZeebeResponse;
import net.engining.zeebe.spring.client.ext.bean.ZeebeResponseBuilder;
import net.engining.zeebe.spring.client.ext.bean.DefaultRequestHeader;
import net.engining.zeebe.spring.client.ext.bean.DefaultResponseHeader;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * 标准化交易流程的请求报文及响应报文
 *
 * @author : Eric Lu
 * @version :
 * @date : 2021-05-26 13:36
 * @since :
 **/
public abstract class AbstractZeebeStarterHandler<E>
        implements ZeebeStarterHandler<ZeebeContext<DefaultRequestHeader, E>>{

    public ZeebeResponse<DefaultResponseHeader, Void> start(
            @NotNull String processId, @NotNull ZeebeContext<DefaultRequestHeader, E> event,
            Integer version, Integer requestTimeout, Integer returnTimeout
    ) {

        Optional<ProcessInstanceEvent> optional = this.defaultStart(
                processId,
                event,
                version,
                requestTimeout,
                returnTimeout
        );

        ProcessInstanceEvent processInstanceEvent;
        ZeebeResponse<DefaultResponseHeader, Void> response;
        DefaultResponseHeader header;
        if (optional.isPresent()){
            processInstanceEvent = optional.get();
            header = new DefaultResponseHeader();
            header.setTimestamp(new Date());
            header.setTxnSerialNo(event.getRequestHead().getTxnSerialNo());
            header.setSvPrSerialNo(processInstanceEvent.getBpmnProcessId());
            response = new ZeebeResponseBuilder<DefaultResponseHeader, Void>()
                    .build()
                    .setResponseHead(header)
                    .setStatusCode(ErrorCode.Success.getValue())
                    .setStatusDesc(ErrorCode.Success.getLabel());
        }
        else {
            header = new DefaultResponseHeader();
            header.setTimestamp(new Date());
            header.setTxnSerialNo(event.getRequestHead().getTxnSerialNo());
            response = new ZeebeResponseBuilder<DefaultResponseHeader, Void>()
                    .build()
                    .setResponseHead(header)
                    .setStatusCode(ErrorCode.UnknowFail.getValue())
                    .setStatusDesc("Can not confirm the process started, please check the error!");
        }

        return response;
    }

    public ZeebeResponse<DefaultResponseHeader, Map<String, Object>> start4Results(
            @NotNull String processId, @NotNull ZeebeContext<DefaultRequestHeader, E> event,
            Integer version, Integer requestTimeout, Integer returnTimeout
    ) {

        Optional<ProcessInstanceResult> optional = this.defaultStart4Results(
                processId,
                event,
                version,
                requestTimeout,
                returnTimeout
        );

        ProcessInstanceResult processInstanceResult;
        ZeebeResponse<DefaultResponseHeader, Map<String, Object>> response;
        DefaultResponseHeader header;
        if (optional.isPresent()){
            processInstanceResult = optional.get();
            header = new DefaultResponseHeader();
            header.setTimestamp(new Date());
            header.setTxnSerialNo(event.getRequestHead().getTxnSerialNo());
            header.setSvPrSerialNo(processInstanceResult.getBpmnProcessId());
            response = new ZeebeResponseBuilder<DefaultResponseHeader,Map<String, Object>>()
                    .build()
                    .setResponseHead(header)
                    .setResponseData(processInstanceResult.getVariablesAsMap())
                    .setStatusCode(ErrorCode.Success.getValue())
                    .setStatusDesc(ErrorCode.Success.getLabel());

        }
        else {
            header = new DefaultResponseHeader();
            header.setTimestamp(new Date());
            header.setTxnSerialNo(event.getRequestHead().getTxnSerialNo());
            response = new ZeebeResponseBuilder<DefaultResponseHeader, Map<String, Object>>()
                    .build()
                    .setResponseHead(header)
                    .setResponseData(Maps.newHashMap())
                    .setStatusCode(ErrorCode.UnknowFail.getValue())
                    .setStatusDesc("Can not get the results from the process, please check the error!");
        }

        return response;
    }
}
