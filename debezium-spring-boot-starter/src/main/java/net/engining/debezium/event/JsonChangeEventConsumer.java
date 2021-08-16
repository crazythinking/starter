package net.engining.debezium.event;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

/**
 * 基于Json 对象封装的Consumer
 *
 * @author : Eric Lu
 * @version :
 * @date : 2021-08-09 15:07
 * @since :
 **/
public class JsonChangeEventConsumer implements DebeziumEngine.ChangeConsumer<ChangeEvent<String, String>>{
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonChangeEventConsumer.class);

    @JsonProperty("key")
    private ChangeEventKey key;

    @Override
    public void handleBatch(
            List<ChangeEvent<String, String>> records,
            DebeziumEngine.RecordCommitter<ChangeEvent<String, String>> committer) throws InterruptedException {
        handleEvents(records, committer);
    }

    private void handleEvents(List<ChangeEvent<String, String>> recordChangeEvents,
                              DebeziumEngine.RecordCommitter<ChangeEvent<String, String>> recordCommitter) throws InterruptedException {
        recordChangeEvents.forEach(recordChangeEvent -> {

            //TODO 调用业务逻辑
            String keyStr = recordChangeEvent.key();
            HashMap<String, Object> keyMap = JSON.parseObject(keyStr, HashMap.class);
            String valueStr = recordChangeEvent.key();
            HashMap<String, Object> valueMap = JSON.parseObject(valueStr, HashMap.class);

            try {
                recordCommitter.markProcessed(recordChangeEvent);
            } catch (InterruptedException e) {
                LOGGER.error("mark record event processed failed");
            }
        });
        recordCommitter.markBatchFinished();
    }

    public static class ChangeEventKey {
        @JsonProperty("schema")
        private SchemaDTO schema;
        @JsonProperty("payload")
        private PayloadDTO payload;

        public SchemaDTO getSchema() {
            return schema;
        }

        public void setSchema(SchemaDTO schema) {
            this.schema = schema;
        }

        public PayloadDTO getPayload() {
            return payload;
        }

        public void setPayload(PayloadDTO payload) {
            this.payload = payload;
        }
    }

    public static class SchemaDTO {
        @JsonProperty("type")
        private String type;
        @JsonProperty("fields")
        private List<FieldsDTO> fields;
        @JsonProperty("optional")
        private Boolean optional;
        @JsonProperty("name")
        private String name;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<FieldsDTO> getFields() {
            return fields;
        }

        public void setFields(List<FieldsDTO> fields) {
            this.fields = fields;
        }

        public Boolean getOptional() {
            return optional;
        }

        public void setOptional(Boolean optional) {
            this.optional = optional;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public static class FieldsDTO {
            @JsonProperty("type")
            private String type;
            @JsonProperty("optional")
            private Boolean optional;
            @JsonProperty("field")
            private String field;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public Boolean getOptional() {
                return optional;
            }

            public void setOptional(Boolean optional) {
                this.optional = optional;
            }

            public String getField() {
                return field;
            }

            public void setField(String field) {
                this.field = field;
            }
        }
    }

    public static class PayloadDTO {
        @JsonProperty("id")
        private Integer id;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }
}
