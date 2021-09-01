package net.engining.datasource.autoconfigure.autotest.jdbc.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.relational.core.conversion.AggregateChange;
import org.springframework.data.relational.core.mapping.event.AbstractRelationalEventListener;
import org.springframework.data.relational.core.mapping.event.AfterLoadEvent;
import org.springframework.data.relational.core.mapping.event.BeforeSaveEvent;
import org.springframework.stereotype.Component;

/**
 * 注意该事件监听器只有在使用 Spring data jdbc Repository 进行操作时才会触发(有点鸡肋！)
 *
 * @author : Eric Lu
 * @version :
 * @date : 2021-08-26 16:10
 * @since :
 **/
@Component
public class OperAdtLogListener extends AbstractRelationalEventListener<OperAdtLog> {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(OperAdtLogListener.class);

    @Override
    protected void onBeforeSave(BeforeSaveEvent<OperAdtLog> event) {
        AggregateChange<OperAdtLog> aggregateChange = event.getAggregateChange();
        LOGGER.debug(aggregateChange.toString());
        super.onBeforeSave(event);
    }

    @Override
    protected void onAfterLoad(AfterLoadEvent<OperAdtLog> event) {
        OperAdtLog operAdtLog = event.getEntity();
        LOGGER.debug(operAdtLog.toString());
        super.onAfterLoad(event);
    }
}
