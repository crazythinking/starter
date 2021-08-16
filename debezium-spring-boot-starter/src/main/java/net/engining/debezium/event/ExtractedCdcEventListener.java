package net.engining.debezium.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-08-11 10:50
 * @since :
 **/
@Component
public class ExtractedCdcEventListener implements ApplicationListener<ExtractedCdcEvent> {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtractedCdcEventListener.class);

    @Async
    @Override
    public void onApplicationEvent(ExtractedCdcEvent event) {
        LOGGER.debug(
                "processTime={}, targetSource={}, targetTrancation={}, targetPayload={}",
                event.getProcessTime(),
                event.getTargetSource(),
                event.getTargetTrancation(),
                event.getTargetRecordData()
        );

        //调用业务处理Flow

    }
}
