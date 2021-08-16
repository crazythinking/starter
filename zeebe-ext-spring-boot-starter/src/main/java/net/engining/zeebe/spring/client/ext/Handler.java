package net.engining.zeebe.spring.client.ext;

import cn.hutool.core.util.StrUtil;
import io.camunda.zeebe.spring.client.ZeebeClientLifecycle;
import net.engining.pg.support.enums.BaseEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

/**
 * @param <E>
 * @param <R>
 * @author Eric Lu
 * @date 2021-05-12 10:21
 **/
public interface Handler<E, R> {

    /**
     * The default value prepended to the log message written <i>before</i> a request is
     * processed.
     */
    String DEFAULT_BEFORE_ZEEBE_PREFIX = "Before Zeebe-request [";

    /**
     * The default value appended to the log message written <i>before</i> a request is
     * processed.
     */
    String DEFAULT_BEFORE_ZEEBE_SUFFIX = "]";

    /**
     * The default value prepended to the log message written <i>after</i> a request is
     * processed.
     */
    String DEFAULT_AFTER_ZEEBE_PREFIX = "After Zeebe-request [";

    /**
     * The default value appended to the log message written <i>after</i> a request is
     * processed.
     */
    String DEFAULT_AFTER_ZEEBE_SUFFIX = "]";

    String SUCCEED = "S";
    String FAILED = "F";

    enum Type implements BaseEnum<String> {

        /**
         * Zeebe Admin
         */
        ADMIN("ADMIN", "TRADE_TYPE:ADMIN"),

        /**
         * Zeebe starter
         */
        STARTER("STARTER", "TRADE_TYPE:STARTER"),

        /**
         * Zeebe worker
         */
        WORKER("WORKER","TRADE_TYPE:WORKER"),

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
     * 设置Zeebe Client; 由实现类注入
     * @return ZeebeClientLifecycle
     */
    ZeebeClientLifecycle getZeebeClientLifecycle();

    /**
     * 设置logger; 由实现类注入
     * @return Logger
     */
    Logger getLogger();

    /**
     * 设置交易类型唯一标识，主要用于监控
     * @return String
     */
    String getTypeId();

    /**
     * Handler前处理切面; 该方法在发送消息到Zeebe Broker之前被调用;
     * 通常用于执行当前节点相关的业务逻辑，并返回相应Response
     * @param   event 消息
     * @return  R response
     */
    R beforeHandler(E event);

    /**
     * 默认Handler前处理切面
     * @param event 消息
     * @param type  处理类所属的角色：生产者，消费者，轮询消费者
     * @param logger 日志操作对象
     * @return R
     */
    default R before(E event, Type type, Logger logger){
        StringBuilder msg = logBuilder(event, type, DEFAULT_BEFORE_ZEEBE_PREFIX);
        msg.append(DEFAULT_BEFORE_ZEEBE_SUFFIX);
        msg.append(StringUtils.SPACE);
        logger.info(msg.toString());

        return beforeHandler(event);
    }

    /**
     * Handler后处理切面; 该方法在发送消息到Zeebe Broker之后被调用
     * @param event 消息
     * @param rt 消息处理成功/失败
     */
    void afterHandler(E event, boolean rt);

    /**
     * 默认Handler后处理切面
     * @param event 消息
     * @param rt 消息处理成功/失败
     * @param type  处理类所属的角色：生产者，消费者，轮询消费者
     * @param logger 日志操作对象
     */
    default void after(E event, boolean rt, Type type, Logger logger){
        afterHandler(event, rt);

        StringBuilder msg = logBuilder(event, type, DEFAULT_AFTER_ZEEBE_PREFIX);
        msg.append(", ");
        if (rt){
            msg.append("TRADE_STATUS:").append(SUCCEED);
        }
        else {
            msg.append("TRADE_STATUS:").append(FAILED);
        }
        msg.append(DEFAULT_AFTER_ZEEBE_SUFFIX);
        msg.append(StringUtils.SPACE);
        logger.info(msg.toString());
    }

    /**
     * @param event     E
     * @param type      Type
     * @param prefix    String
     * @return          StringBuilder
     */
    default StringBuilder logBuilder(E event, Type type, String prefix) {
        StringBuilder msg = new StringBuilder();
        msg.append(prefix);
        msg.append(type.label);
        msg.append(StrUtil.DASHED);
        msg.append(getTypeId());
        if (Type.ADMIN.equals(type)){
            msg.append(StrUtil.DASHED);
            msg.append(event.toString());
        }
        return msg;
    }

}
