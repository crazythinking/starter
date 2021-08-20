package net.engining.debezium.event;

import java.io.Serializable;
import java.util.Map;

/**
 * 提取后的 CDC Event 数据对象
 *
 * @author : Eric Lu
 * @version :
 * @date : 2021-08-18 22:16
 * @since :
 **/
public class ExtractedCdcEventBo implements Serializable {

    /**
     * 接收到CDC事件的时间戳
     */
    private long processTime;

    /**
     * 操作符
     */
    private String operation;

    /**
     * CDC事件的数据源描述信息，可用于标识唯一性
     */
    private Map<String, Object> targetSource;

    /**
     * CDC事件的事务相关描述信息
     */
    private Map<String, Object> targetTrancation;

    /**
     * 表记录数据
     */
    private Map<String, Object> targetRecordData;

    /**
     * 从targetSource中提取，用于作为Event的唯一标识
     */
    private String identifyKey;

    /**
     * 从targetSource中提取，DB名称
     */
    private String db;

    /**
     * 从targetSource中提取，table名称
     */
    private String table;

    /**
     * 从targetSource中提取，schema名称
     */
    private String schema;

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public void setIdentifyKey(String identifyKey) {
        this.identifyKey = identifyKey;
    }

    public String getIdentifyKey() {
        return identifyKey;
    }

    public long getProcessTime() {
        return processTime;
    }

    public void setProcessTime(long processTime) {
        this.processTime = processTime;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Map<String, Object> getTargetSource() {
        return targetSource;
    }

    public void setTargetSource(Map<String, Object> targetSource) {
        this.targetSource = targetSource;
    }

    public Map<String, Object> getTargetTrancation() {
        return targetTrancation;
    }

    public void setTargetTrancation(Map<String, Object> targetTrancation) {
        this.targetTrancation = targetTrancation;
    }

    public Map<String, Object> getTargetRecordData() {
        return targetRecordData;
    }

    public void setTargetRecordData(Map<String, Object> targetRecordData) {
        this.targetRecordData = targetRecordData;
    }

    @Override
    public String toString() {
        return "ExtractedCdcEventBo{" +
                "processTime=" + processTime +
                ", operation='" + operation + '\'' +
                ", targetSource=" + targetSource +
                ", targetTrancation=" + targetTrancation +
                ", targetRecordData=" + targetRecordData +
                ", identifyKey='" + identifyKey + '\'' +
                ", db='" + db + '\'' +
                ", table='" + table + '\'' +
                ", schema='" + schema + '\'' +
                '}';
    }
}
