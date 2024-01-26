package net.engining.debezium.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.data.Envelope;
import net.engining.bustream.base.stream.AbstractConsumeBustreamHandler;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.apache.kafka.connect.errors.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static io.debezium.data.Envelope.FieldName.*;

/**
 * 基于 Debezium Kafka Connect 获取 CDC Event，存入 Kafka Broker 后，从 Kafka 获取消息的 Consumer；
 *
 * @author : Eric Lu
 * @version :
 * @since : 2024-01-23 10:33
 **/
public class CdcConsumerFuncStreamHandler extends AbstractConsumeBustreamHandler<HashMap<String, Object>> implements InitializingBean {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(CdcConsumerFuncStreamHandler.class);

    ApplicationContext applicationContext;

    ObjectMapper objectMapper;

    public static final String OPERATION = "op";

    public CdcConsumerFuncStreamHandler(ApplicationContext applicationContext, ObjectMapper objectMapper) {
        this.applicationContext = applicationContext;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void handler(HashMap<String, Object> event, Map<String, Object> headers) throws Exception {
        //解析CDC事件
        //CDC事件的value对应的具体数据：对应表字段值数据，包括before|after|source|op|ts_ms|transaction
        if (ValidateUtilExt.isNotNullOrEmpty(event)) {
            String op = null;
            try {
                //’op‘操作符可能未包含在CDC事件定义内
                op = (String) event.get(OPERATION);
            }
            catch (DataException e) {
                LOGGER.warn(e.getMessage());
            }

            if (ValidateUtilExt.isNotNullOrEmpty(op)){
                //只关心操作符枚举内定义的操作,判断操作的类型.过滤掉读,只处理增删改
                if (!op.equals(Envelope.Operation.READ.code())) {
                    //封装到ApplicationEvent发布事件,原始数据向下传递,兼容下游处理
                    ExtractedCdcEvent extractedCdcEvent = new ExtractedCdcEvent(event);
                    ExtractedCdcEventBo extractedCdcEventBo = new ExtractedCdcEventBo();
                    extractedCdcEventBo.setOperation(op);

                    String record = null;
                    if (op.equals(Envelope.Operation.DELETE.code()) || op.equals(Envelope.Operation.TRUNCATE.code())){
                        record = BEFORE;
                    }
                    else if (op.equals(Envelope.Operation.CREATE.code())) {
                        record = AFTER;
                    }
                    //for update operation
                    else {
                        HashMap<String, Object> before = (HashMap<String, Object>) event.get(BEFORE);
                        HashMap<String, Object> after = (HashMap<String, Object>) event.get(AFTER);
                        if (objectMapper.writeValueAsString(before).equals(objectMapper.writeValueAsString(after))){
                            //丢弃不需要处理
                            LOGGER.warn("The values of the included columns of update operation are the same, drop it!");
                        }
                        else {
                            record = AFTER;
                        }
                    }

                    // 获取增删改对应的结构体数据
                    if (ValidateUtilExt.isNotNullOrEmpty(record)){
                        HashMap<String, Object> targetRecordData = (HashMap<String, Object>) event.get(record);
                        if (ValidateUtilExt.isNotNullOrEmpty(targetRecordData)){
                            extractedCdcEventBo.setTargetRecordData(targetRecordData);
                        }

                        //CDC事件的数据源描述信息，可用于标识唯一性
                        HashMap<String, Object> targetSource = (HashMap<String, Object>) event.get(SOURCE);
                        if (ValidateUtilExt.isNotNullOrEmpty(targetSource)){
                            extractedCdcEventBo.setTargetSource(targetSource);
                            //CDC事件发生的时间戳: 取自value.source中的"ts_ms", 但该值的毫秒部分被舍弃了
                            long eventTime = (long) targetSource.get(TIMESTAMP);
                            extractedCdcEventBo.setEventTime(eventTime);
                        }

                        //接收到CDC事件的时间戳: 取自value中的"ts_ms"
                        long processTime = (long) event.get(TIMESTAMP);
                        extractedCdcEventBo.setProcessTime(processTime);

                        //CDC事件的事务相关描述信息
                        HashMap<String, Object> targetTrancation = (HashMap<String, Object>) event.get(TRANSACTION);
                        if (ValidateUtilExt.isNotNullOrEmpty(targetTrancation)){
                            extractedCdcEventBo.setTargetTrancation(targetTrancation);
                        }

                        //装载数据体
                        extractedCdcEvent.setCdcEventBo(extractedCdcEventBo);
                        //发布事件
                        applicationContext.publishEvent(extractedCdcEvent);
                    }
                    //not have valid record

                }
                else {
                    LOGGER.warn("only handle with insertion, deletion, or modification events; this op={}", op);
                }
            }
            //not have "op"
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.setLogger(LOGGER);
    }

}
