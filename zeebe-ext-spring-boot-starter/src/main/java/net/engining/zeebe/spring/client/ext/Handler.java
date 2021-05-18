package net.engining.zeebe.spring.client.ext;

import cn.hutool.core.util.StrUtil;
import io.camunda.zeebe.spring.client.ZeebeClientLifecycle;
import net.engining.pg.support.enums.BaseEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

/**
 * @author Eric Lu
 * @date 2021-05-12 10:21
 **/
public interface Handler<E> {

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
     */
    ZeebeClientLifecycle getZeebeClientLifecycle();

    /**
     * 设置logger; 由实现类注入
     */
    Logger getLogger();

    /**
     * 设置交易类型唯一标识，主要用于监控
     */
    String getTypeId();

    /**
     * Handler前处理切面
     * @param event 消息
     */
    void before(E event);

    /**
     * 默认Handler前处理切面
     * @param event 消息
     * @param type  处理类所属的角色：生产者，消费者，轮询消费者
     * @param logger 日志操作对象
     */
    default void defalutBefore(E event, Type type, Logger logger){
        StringBuilder msg = new StringBuilder();
        msg.append(DEFAULT_BEFORE_ZEEBE_PREFIX);
        msg.append(type.label);
        msg.append(StrUtil.DASHED);
        msg.append(getTypeId());
        msg.append(StrUtil.DASHED);
        if (Type.ADMIN.equals(type)){
            msg.append(event.toString());
        }
        else {
            msg.append(event.getClass().getCanonicalName());
        }
        msg.append(DEFAULT_BEFORE_ZEEBE_SUFFIX);
        msg.append(StringUtils.SPACE);
        logger.info(msg.toString());

        before(event);
    }

    /**
     * Handler后处理切面
     * @param event 消息
     * @param rt 消息处理成功/失败
     */
    void after(E event, boolean rt);

    /**
     * 默认Handler后处理切面
     * @param event 消息
     * @param rt 消息处理成功/失败
     * @param type  处理类所属的角色：生产者，消费者，轮询消费者
     * @param logger 日志操作对象
     */
    default void defaultAfter(E event, boolean rt, Type type, Logger logger){
        after(event, rt);

        StringBuilder msg = new StringBuilder();
        msg.append(DEFAULT_AFTER_ZEEBE_PREFIX);
        msg.append(type.label);
        msg.append(StrUtil.DASHED);
        msg.append(getTypeId());
        msg.append(StrUtil.DASHED);
        if (Type.ADMIN.equals(type)){
            msg.append(event.toString());
        }
        else {
            msg.append(event.getClass().getCanonicalName());
        }
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

}
