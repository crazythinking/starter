package net.engining.debezium.event;

import org.springframework.context.ApplicationEvent;

import java.util.Map;

/**
 * 提取后的 CDC Event
 *
 * @author : Eric Lu
 * @version :
 * @date : 2021-08-10 18:07
 * @since :
 **/
public class ExtractedCdcEvent extends ApplicationEvent {

    /**
     * 接收到CDC事件的时间戳
     */
    long processTime;

    /**
     * 操作符
     */
    String operation;

    /**
     * CDC事件的数据源描述信息，可用于标识唯一性
     */
    Map<String, Object> targetSource;

    /**
     * CDC事件的事务相关描述信息
     */
    Map<String, Object> targetTrancation;

    /**
     * 表记录数据
     */
    Map<String, Object> targetRecordData;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public ExtractedCdcEvent(Object source) {
        super(source);
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setProcessTime(long processTime) {
        this.processTime = processTime;
    }

    public void setTargetSource(Map<String, Object> targetSource) {
        this.targetSource = targetSource;
    }

    public void setTargetTrancation(Map<String, Object> targetTrancation) {
        this.targetTrancation = targetTrancation;
    }

    public void setTargetRecordData(Map<String, Object> targetRecordData) {
        this.targetRecordData = targetRecordData;
    }

    public long getProcessTime() {
        return processTime;
    }

    public Map<String, Object> getTargetSource() {
        return targetSource;
    }

    public Map<String, Object> getTargetTrancation() {
        return targetTrancation;
    }

    public Map<String, Object> getTargetRecordData() {
        return targetRecordData;
    }

}
