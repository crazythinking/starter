package net.engining.datasource.autoconfigure.autotest.support;

import net.engining.datasource.autoconfigure.support.TransactionalEvent;
import net.engining.datasource.autoconfigure.support.Utils;
import net.engining.gm.entity.dto.OperAdtLogDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-08-31 14:27
 * @since :
 **/
@Component
public class TransactionalListener {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionalListener.class);

    @Async(Utils.DB_EVENT_LISTENER)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterSavedOperAdtLog(TransactionalEvent<OperAdtLogDto> event){
        assert event.getEntity() != null;
        LOGGER.debug(event.getEntity().toString());
    }
}
