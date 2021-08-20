package net.engining.debezium.event;

import org.springframework.context.ApplicationEvent;

/**
 * 提取后的 CDC ApplicationEvent
 *
 * @author : Eric Lu
 * @version :
 * @date : 2021-08-10 18:07
 * @since :
 **/
public class ExtractedCdcEvent extends ApplicationEvent {

    /**
     * CDC Event 数据对象
     */
    private ExtractedCdcEventBo cdcEventBo;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public ExtractedCdcEvent(Object source) {
        super(source);
    }

    public ExtractedCdcEventBo getCdcEventBo() {
        return cdcEventBo;
    }

    public void setCdcEventBo(ExtractedCdcEventBo cdcEventBo) {
        this.cdcEventBo = cdcEventBo;
    }

    @Override
    public String toString() {
        return "ExtractedCdcEvent{" +
                "cdcEventBo=" + cdcEventBo +
                '}';
    }
}
