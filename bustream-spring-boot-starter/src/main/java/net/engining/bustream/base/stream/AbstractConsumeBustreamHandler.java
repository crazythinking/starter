package net.engining.bustream.base.stream;

import net.engining.bustream.base.BustreamHandler;
import net.engining.pg.support.core.exception.ErrorCode;
import net.engining.pg.support.core.exception.ErrorMessageException;
import net.engining.pg.support.utils.ExceptionUtilsExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.MessageHeaders;

import java.io.Serializable;
import java.util.Map;

/**
 * 默认消息消费者的处理抽象类，由业务系统继承并注入 Spring Context；
 *
 * @author Eric Lu
 * @date 2020-10-29 18:16
 **/
public abstract class AbstractConsumeBustreamHandler<E extends Serializable> implements BustreamHandler<E> {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConsumeBustreamHandler.class);

    Logger logger = LOGGER;

    Type type = Type.CONSUMER;

    @Override
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * 从消息中间件获取消息并消费
     * @param event     payload
     * @param headers   消息头
     */
    protected void consume(E event, MessageHeaders headers) {
        logger.trace("consume message headers: {}", headers);
        logger.trace("consume event for {}: {}", event.getClass(), event);
        boolean ret = received(event, headers);
        if (!ret){
            throw new ErrorMessageException(ErrorCode.SystemError, "handle msg error");
        }
    }

    /**
     * 接收到消息，并调用业务逻辑
     * @param event 消息事件
     */
    protected boolean received(E event, MessageHeaders headers){
        defaultBefore(event, type, logger);
        try {
            handler(event, headers);
            defaultAfter(event, true, type, logger);
            return true;
        }
        //FIXME 不因该捕捉所有异常，会造成非业务异常无法重试；比如超时类的异常
        catch (Exception e) {
            ExceptionUtilsExt.dump(e);
            defaultAfter(event, false, type, logger);
        }
        return false;
    }

    /**
     * 接收到消息事件后的处理逻辑
     * @param event     消息事件
     * @param headers   头信息
     * @exception       Exception
     */
    protected abstract void handler(E event, MessageHeaders headers) throws Exception;

    @Override
    public void before(E event) {
        //do nothing
    }

    @Override
    public void after(E event, boolean rt) {
        //do nothing
    }

}
