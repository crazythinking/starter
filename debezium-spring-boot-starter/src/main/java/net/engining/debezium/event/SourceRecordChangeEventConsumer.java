package net.engining.debezium.event;

import io.debezium.data.Envelope;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.header.Headers;
import org.apache.kafka.connect.source.SourceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;

import static io.debezium.data.Envelope.FieldName.*;
import static java.util.stream.Collectors.toMap;

/**
 * 基于Kafka Connect API SourceRecord 对象封装的Consumer
 *
 * @author : Eric Lu
 * @version :
 * @date : 2021-08-09 14:12
 * @since :
 **/
@SuppressWarnings("AlibabaMethodTooLong")
public class SourceRecordChangeEventConsumer implements DebeziumEngine.ChangeConsumer<RecordChangeEvent<SourceRecord>> {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(SourceRecordChangeEventConsumer.class);

    ApplicationContext applicationContext;

    public SourceRecordChangeEventConsumer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void handleBatch(
            List<RecordChangeEvent<SourceRecord>> records,
            DebeziumEngine.RecordCommitter<RecordChangeEvent<SourceRecord>> committer) throws InterruptedException {
        LOGGER.info("Captured {} CDC Events, start processing!", records.size());
        handleEvents(records, committer);
        LOGGER.info("CDC Events processing finished and committed!");
    }

    private void handleEvents(List<RecordChangeEvent<SourceRecord>> recordChangeEvents,
                              DebeziumEngine.RecordCommitter<RecordChangeEvent<SourceRecord>> recordCommitter)
            throws InterruptedException {
        recordChangeEvents.forEach(recordChangeEvent -> {
            //获取一条CDC事件信息
            SourceRecord sourceRecord = recordChangeEvent.record();
            //解析CDC事件
            Map<String, ?> partitionMap = sourceRecord.sourcePartition();
            //该Map内的值组合在一起用于标识唯一的CDC事件
            Map<String, ?> offsetMap = sourceRecord.sourceOffset();
            //xxljob-mysql-cdc.xxljob.xxl_job_group
            String topic = sourceRecord.topic();
            Integer partition = sourceRecord.kafkaPartition();
            //CDC事件的key对应的Schema：对应表的主键字段定义数据
            Schema keySchema = sourceRecord.keySchema();
            //CDC事件的key对应的具体数据：对应表的主键字段值数据
            Struct keyStruct = (Struct) sourceRecord.key();
            //CDC事件的value对应的Schema：对应表的字段定义数据
            Schema valueSchema = sourceRecord.valueSchema();
            //CDC事件的value对应的具体数据：对应表的字段值数据，包括before|after|source|op|ts_ms|transaction
            Struct valueStruct = (Struct) sourceRecord.value();
            Long tsms = sourceRecord.timestamp();
            Headers headers = sourceRecord.headers();

            if (ValidateUtilExt.isNotNullOrEmpty(valueStruct)) {
                String op = null;
                try {
                    //’op‘操作符可能未包含在CDC事件定义内
                    op = (String) valueStruct.get(OPERATION);
                }
                catch (DataException e) {
                    LOGGER.warn(e.getMessage());
                }

                if (ValidateUtilExt.isNotNullOrEmpty(op)){
                    //只关心操作符枚举内定义的操作,判断操作的类型.过滤掉读,只处理增删改
                    if (!op.equals(Envelope.Operation.READ.code())) {
                        //封装到ApplicationEvent发布事件,原始数据向下传递,兼容下游处理
                        ExtractedCdcEvent extractedCdcEvent = new ExtractedCdcEvent(sourceRecord);
                        ExtractedCdcEventBo extractedCdcEventBo = new ExtractedCdcEventBo();
                        extractedCdcEventBo.setOperation(op);

                        String record = (
                                op.equals(Envelope.Operation.DELETE.code())
                                || op.equals(Envelope.Operation.TRUNCATE.code())
                        ) ? BEFORE : AFTER;

                        // 获取增删改对应的结构体数据
                        Struct recordDataStruct = (Struct) valueStruct.get(record);
                        if (ValidateUtilExt.isNotNullOrEmpty(recordDataStruct)){
                            // 将变更的行封装为Map
                            Map<String, Object> targetRecordData = recordDataStruct.schema().fields().stream()
                                    .map(Field::name)
                                    .filter(fieldName -> recordDataStruct.get(fieldName) != null)
                                    .map(fieldName -> Pair.of(fieldName, recordDataStruct.get(fieldName)))
                                    .collect(toMap(Pair::getKey, Pair::getValue));
                            extractedCdcEventBo.setTargetRecordData(targetRecordData);
                        }

                        //CDC事件的数据源描述信息，可用于标识唯一性
                        Struct sourceStruct = (Struct) valueStruct.get(SOURCE);
                        if (ValidateUtilExt.isNotNullOrEmpty(sourceStruct)){
                            Map<String, Object> targetSource = sourceStruct.schema().fields().stream()
                                    .map(Field::name)
                                    .filter(fieldName -> sourceStruct.get(fieldName) != null)
                                    .map(fieldName -> Pair.of(fieldName, sourceStruct.get(fieldName)))
                                    .collect(toMap(Pair::getKey, Pair::getValue));
                            extractedCdcEventBo.setTargetSource(targetSource);
                            //CDC事件发生的时间戳: 取自value.source中的"ts_ms", 但该值的毫秒部分被舍弃了
                            long eventTime = (long) targetSource.get(TIMESTAMP);
                            extractedCdcEventBo.setEventTime(eventTime);
                        }

                        //接收到CDC事件的时间戳: 取自value中的"ts_ms"
                        long processTime = (long) valueStruct.get(TIMESTAMP);
                        extractedCdcEventBo.setProcessTime(processTime);

                        //CDC事件的事务相关描述信息
                        Struct trancationStruct = (Struct) valueStruct.get(TRANSACTION);
                        if (ValidateUtilExt.isNotNullOrEmpty(trancationStruct)){
                            Map<String, Object> targetTrancation = trancationStruct.schema().fields().stream()
                                    .map(Field::name)
                                    .filter(fieldName -> trancationStruct.get(fieldName) != null)
                                    .map(fieldName -> Pair.of(fieldName, trancationStruct.get(fieldName)))
                                    .collect(toMap(Pair::getKey, Pair::getValue));
                            extractedCdcEventBo.setTargetTrancation(targetTrancation);
                        }

                        //装载数据体
                        extractedCdcEvent.setCdcEventBo(extractedCdcEventBo);
                        //发布事件
                        applicationContext.publishEvent(extractedCdcEvent);
                    }
                    else {
                        LOGGER.warn("only handle with insertion, deletion, or modification events");
                    }
                }
                //not have "op"

            }
            //not have "valueStruct"


            //置处理完成标志，通知框架完成offset记录
            try {
                recordCommitter.markProcessed(recordChangeEvent);
            } catch (InterruptedException e) {
                LOGGER.error("mark record event processed failed");
            }
        });
        recordCommitter.markBatchFinished();
    }
}
