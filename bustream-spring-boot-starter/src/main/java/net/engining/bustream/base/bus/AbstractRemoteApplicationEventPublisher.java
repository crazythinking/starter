package net.engining.bustream.base.bus;

import net.engining.bustream.base.BustreamHandler;
import net.engining.gm.config.props.BustreamProperties;
import net.engining.pg.support.utils.ExceptionUtilsExt;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.bus.BusProperties;
import org.springframework.context.ApplicationEventPublisher;

import java.io.Serializable;

/**
 * Bus模式下，支持泛型的远程消息事件的发布器, 用于作为GenericRemoteApplicationEvent的远程事件生产者
 *
 * @author : Eric Lu
 * @version :
 * @date : 2020-11-04 13:47
 * @since :
 **/
public abstract class AbstractRemoteApplicationEventPublisher<E extends Serializable> implements BustreamHandler<E> {
    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRemoteApplicationEventPublisher.class);

    @Autowired
    private BusProperties busProperties;

    @Autowired
    private BustreamProperties bustreamProperties;

    @Autowired
    private ApplicationEventPublisher publisher;

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
     * 发布消息事件，需要业务逻辑端显示调用
     *
     * @param event       消息事件
     * @param destination 发布的目的地，格式如下：<br>
     *                    spring.application.name(发布到指定微服务的所有实例),
     *                    或 spring.application.name:applicationContextId(发布到指定微服务的指定实例)
     */
    public void publish(E event, String destination) {
        defalutBefore(event, Type.PUBLISHER, LOGGER);
        try {
            transform(event);
            if (ValidateUtilExt.isNullOrEmpty(destination)) {
                destination = bustreamProperties.defaultBusDestination;
            }

            publisher.publishEvent(
                    new GenericRemoteApplicationEvent<E>(this, event, busProperties.getId(), destination)
            );

            defaultAfter(event, true, Type.PUBLISHER, LOGGER);
        } catch (Exception e) {
            ExceptionUtilsExt.dump(e);
            defaultAfter(event, false, Type.PUBLISHER, LOGGER);
        }
    }

    /**
     * 发布消息事件前的业务处理逻辑
     *
     * @param event 消息事件
     */
    public abstract void transform(E event);
}
