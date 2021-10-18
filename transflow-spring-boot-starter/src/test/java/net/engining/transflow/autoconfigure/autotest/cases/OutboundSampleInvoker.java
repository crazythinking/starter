package net.engining.transflow.autoconfigure.autotest.cases;

import cn.hutool.core.lang.Console;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import net.engining.control.api.key.GlobleIdKey;
import net.engining.control.api.key.InboundIdKey;
import net.engining.control.api.key.OutAsynIndKey;
import net.engining.control.api.key.OutMqMsgIdKey;
import net.engining.control.api.key.OutRequestMsgKey;
import net.engining.control.api.key.OutRequestUrlKey;
import net.engining.control.api.key.OutSignTokenKey;
import net.engining.control.api.key.OutSvPrIdKey;
import net.engining.control.api.key.OutTransCodeKey;
import net.engining.control.api.key.OutTransStatusKey;
import net.engining.control.api.key.OutTransVersionKey;
import net.engining.control.api.key.OutTxnDateTimeKey;
import net.engining.control.api.key.SendOutResponseMsgKey;
import net.engining.control.core.flow.FlowContext;
import net.engining.control.core.invoker.AbstractSkippableInvoker;
import net.engining.control.core.invoker.InvokerDefinition;
import net.engining.control.core.invoker.Outboundable;

import java.util.Map;

/**
 * outbound invoker test
 * 要求：
 * 1.必须继承AbstractSkippableOutboundableInvoker，并重写相关方法
 * 2.requires中的字段不允许减少，可以在readySendData中进行赋值
 * 3.optional中字段为非必输字段，可以在readySendData中尽量赋值
 * 4.执行顺序为：prepare->prepare2Customrized->必输字段检查->beforeOutboundGoing->invoker->afterOutboundBacking->outboudingBacking
 * 5.注意：如果isAsync为ture，则不会执行afterOutboundBacking->outboudingBacking
 *
 * @author ChenBao
 * @version 1.0
 * @date 2021/4/1 13:35
 * @since 1.0
 */
@InvokerDefinition(
        name = "Outbound Sample Test Invoker ",
        requires = {
                //以下字段为必输字段
                //默认请求流水的id
                InboundIdKey.class,
                GlobleIdKey.class,
                OutSvPrIdKey.class,
                //根据isAsync进行赋值
                OutAsynIndKey.class,
                OutTxnDateTimeKey.class,
                OutRequestMsgKey.class
        },
        optional = {
                OutSignTokenKey.class,
                OutTransCodeKey.class,
                OutMqMsgIdKey.class,
                OutTransStatusKey.class,
                OutTransVersionKey.class,
                OutRequestUrlKey.class,

        }

)
public class OutboundSampleInvoker extends AbstractSkippableInvoker implements Outboundable {

    @Override
    public void invoke(FlowContext flowContext) {
        Console.log("执行invoker");
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void outboudingBacking(FlowContext ctx) {
        Console.log("外部服务返回逻辑，只用于同步调用的返回");
        Map<String, String> sendMap = Maps.newHashMap();
        sendMap.put("name", "李四");
        ctx.put(SendOutResponseMsgKey.class, JSON.toJSONString(sendMap));
    }

    @Override
    public void prepare2Customrized(FlowContext flowContext) {
        flowContext.put(OutSvPrIdKey.class, "SCAC");
        Map<String, String> sendMap = Maps.newHashMap();
        sendMap.put("name", "张三");
        flowContext.put(OutRequestMsgKey.class, JSON.toJSONString(sendMap));

        //覆盖默认值
        flowContext.put(OutTransCodeKey.class, "SCAC001");

    }

}
