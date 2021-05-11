package net.engining.bustream.base;

import net.engining.pg.support.enums.BaseEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.Serializable;

/**
 * 消息事件 Handler接口
 *
 * @author Eric Lu
 * @date 2020-10-29 18:32
 **/
public interface BustreamHandler<E extends Serializable> {

    enum Type implements BaseEnum<String> {

        /**
         * 生产者
         */
        PRODUCER("PRODUCER", "TRADE_TYPE:PRODUCER-"),

        /**
         * 轮询消费者
         */
        POLLABLE_CONSUMER("POLLABLE_CONSUMER","TRADE_TYPE:POLLABLE_CONSUMER-"),

        /**
         * 消费者
         */
        CONSUMER("CONSUMER","TRADE_TYPE:CONSUMER-"),

        /**
         * Bus event 监听者
         */
        LISTENER("LISTENER","TRADE_TYPE:LISTENER-"),

        /**
         * Bus event 发布者
         */
        PUBLISHER("PUBLISHER","TRADE_TYPE:PUBLISHER-"),
        ;

        private final String value;

        private final String label;

        Type(String value, String label) {
            this.value = value;
            this.label = label;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String getLabel() {
            return label;
        }
    }

    /**
     * The default value prepended to the log message written <i>before</i> a request is
     * processed.
     */
    String DEFAULT_BEFORE_MESSAGE_PREFIX = "Before MQ-request [";

    /**
     * The default value appended to the log message written <i>before</i> a request is
     * processed.
     */
    String DEFAULT_BEFORE_MESSAGE_SUFFIX = "]";

    /**
     * The default value prepended to the log message written <i>after</i> a request is
     * processed.
     */
    String DEFAULT_AFTER_MESSAGE_PREFIX = "After MQ-request [";

    /**
     * The default value appended to the log message written <i>after</i> a request is
     * processed.
     */
    String DEFAULT_AFTER_MESSAGE_SUFFIX = "]";

    String SUCCEED = "S";
    String FAILED = "F";

    /**
     * 设置logger
     */
    void setLogger(Logger logger);

    /**
     * 设置处理器类型
     * @param type  {@link Type} 处理器类型
     */
    void setType(Type type);

    /**
     * 消息事件发送或接收前处理切面
     * @param event 消息
     */
    void before(E event);

    /**
     * 消息事件发送或接收前处理切面
     * @param event 消息
     * @param type  处理类所属的角色：生产者，消费者，轮询消费者
     * @param logger 日志操作对象
     */
    default void defalutBefore(E event, Type type, Logger logger){
        String msg = DEFAULT_BEFORE_MESSAGE_PREFIX +
                type.label +
                event.getClass().getCanonicalName() +
                DEFAULT_BEFORE_MESSAGE_SUFFIX +
                StringUtils.SPACE;
        logger.info(msg);
    }

    /**
     * 消息事件发送或接收后处理切面
     * @param event 消息
     * @param rt 消息处理成功/失败
     */
    void after(E event, boolean rt);

    /**
     * 消息事件发送或接收后处理切面
     * @param event 消息
     * @param rt 消息处理成功/失败
     * @param type  处理类所属的角色：生产者，消费者，轮询消费者
     * @param logger 日志操作对象
     */
    default void defaultAfter(E event, boolean rt, Type type, Logger logger){
        StringBuilder msg = new StringBuilder();
        msg.append(DEFAULT_AFTER_MESSAGE_PREFIX);
        msg.append(type.label);
        msg.append(event.getClass().getCanonicalName());
        msg.append(", ");
        if (rt){
            msg.append("TRADE_STATUS:").append(SUCCEED);
        }
        else {
            msg.append("TRADE_STATUS:").append(FAILED);
        }

        msg.append(DEFAULT_AFTER_MESSAGE_SUFFIX);
        msg.append(StringUtils.SPACE);
        logger.info(msg.toString());
    }

}
