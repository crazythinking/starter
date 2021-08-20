package net.engining.debezium.autoconfigure.autotest.support;

import net.engining.debezium.event.AbstractExtractedCdcEventListener;
import net.engining.debezium.event.DbzRequestHeader;
import net.engining.debezium.event.ExtractedCdcEventBo;
import net.engining.pg.web.bean.CommonWithHeaderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-08-19 15:45
 * @since :
 **/
@Component
public class SimpleCdcEventListener extends AbstractExtractedCdcEventListener {

    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleCdcEventListener.class);

    @Override
    public void eventDispatcher(CommonWithHeaderRequest<DbzRequestHeader, ExtractedCdcEventBo> request) {
        LOGGER.info("Request Header: {}, Event Bean: {}", request.getRequestHead(), request.getRequestData());
    }
}
