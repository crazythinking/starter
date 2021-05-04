package net.engining.bustream.base.stream;

import net.engining.bustream.base.BustreamHandler;
import net.engining.pg.support.utils.ExceptionUtilsExt;

import java.io.Serializable;
import java.util.Map;

/**
 * 默认消息消费者的处理抽象类，由业务系统继承并注入 Spring Context；
 *
 * @author Eric Lu
 * @date 2020-10-29 18:16
 **/
public abstract class AbstractConsumeBustreamHandler<E extends Serializable> implements BustreamHandler<E> {

    /**
     * 接收到消息，并调用业务逻辑
     * @param event 消息事件
     */
    boolean received(E event, Map<String, Object> headers){
        before(event);
        try {
            handler(event, headers);
            after(event, true);
            return true;
        } catch (Exception e) {
            ExceptionUtilsExt.dump(e);
            after(event, false);
        }
        return false;
    }

    /**
     * 接收到消息事件后的处理逻辑
     * @param event     消息事件
     * @param headers   头信息
     */
    protected abstract void handler(E event, Map<String, Object> headers) throws Exception;

}
