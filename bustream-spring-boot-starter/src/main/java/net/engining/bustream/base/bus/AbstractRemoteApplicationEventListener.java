package net.engining.bustream.base.bus;

import net.engining.bustream.base.BustreamHandler;
import net.engining.pg.support.utils.ExceptionUtilsExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;

import java.io.Serializable;

/**
 * Bus模式下，支持泛型的远程消息事件的监听器, 用于作为GenericRemoteApplicationEvent的远程事件消费者
 * @author : Eric Lu
 * @version :
 * @date : 2020-10-30 16:05
 * @since :
 **/
public abstract class AbstractRemoteApplicationEventListener<E extends Serializable> implements BustreamHandler<E> {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRemoteApplicationEventListener.class);

    @Override
    public void setLogger(Logger logger) {
        //do nothing
    }

    @Override
    public void setType(Type type) {
        //do nothing
    }

    @Override
    public void before(E event) {
        //do nothing
    }

    @Override
    public void after(E event, boolean rt) {
        //do nothing
    }

    /**
     * 接收到消息事件后的处理逻辑
     * @param event 消息事件
     * @return true: 业务逻辑处理成功
     */
    public abstract boolean handler(E event);

    @EventListener
    public void onEvent4Generic(GenericRemoteApplicationEvent<E> event) {
        defaultBefore(event.getTarget(), Type.LISTENER, LOGGER);
        try {
            boolean rt = handler(event.getTarget());
            defaultAfter(event.getTarget(), rt, Type.LISTENER, LOGGER);
        } catch (Exception e) {
            ExceptionUtilsExt.dump(e);
            defaultAfter(event.getTarget(), false, Type.LISTENER, LOGGER);
        }

    }
}
