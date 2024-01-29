package net.engining.debezium.prop.utils;

import net.engining.debezium.event.CdcConsumerFuncStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.messaging.Message;

import java.util.HashMap;
import java.util.Objects;

/**
 * @author : Eric Lu
 * @version :
 * @since : 2024-01-29 9:02
 **/
public class DebeziumUtils {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(DebeziumUtils.class);

    public static void consumeFunc(ObjectProvider<CdcConsumerFuncStreamHandler> streamHandler,
                                   Message<HashMap<String, Object>> message
    ) {
        Object v = message.getPayload();
        String className = v.getClass().getName();
        if (className.equals("org.springframework.kafka.support.KafkaNull")) {
            // this is a tombstone record
            LOGGER.info("Received tombstone record, skipped");
        }
        else {
            try {
                Objects.requireNonNull(streamHandler.getIfAvailable()).consume(message.getPayload(), message.getHeaders());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
