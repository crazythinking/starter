package net.engining.datasource.autoconfigure.autotest.jdbc.support;

import org.springframework.data.relational.core.conversion.AggregateChange;
import org.springframework.data.relational.core.mapping.event.AbstractRelationalEventListener;
import org.springframework.data.relational.core.mapping.event.AfterLoadEvent;
import org.springframework.data.relational.core.mapping.event.BeforeSaveEvent;
import org.springframework.stereotype.Component;

/**
 * 该事件监听器只有在使用Spring data进行操作时才会触发
 * @author : Eric Lu
 * @version :
 * @date : 2021-08-26 16:10
 * @since :
 **/
@Component
public class OperAdtLogListener extends AbstractRelationalEventListener<OperAdtLog> {

    @Override
    protected void onBeforeSave(BeforeSaveEvent<OperAdtLog> event) {
        AggregateChange<OperAdtLog> aggregateChange = event.getAggregateChange();
        super.onBeforeSave(event);
    }

    @Override
    protected void onAfterLoad(AfterLoadEvent<OperAdtLog> event) {
        OperAdtLog operAdtLog = event.getEntity();
        super.onAfterLoad(event);
    }
}
