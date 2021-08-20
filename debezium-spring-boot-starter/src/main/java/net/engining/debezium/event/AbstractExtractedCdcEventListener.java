package net.engining.debezium.event;

import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Joiner;
import net.engining.pg.support.db.DbConstants;
import net.engining.pg.web.CommonWithHeaderRequestBuilder;
import net.engining.pg.web.bean.CommonWithHeaderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;

import java.util.Date;
import java.util.Map;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-08-11 10:50
 * @since :
 **/
public abstract class AbstractExtractedCdcEventListener implements ApplicationListener<ExtractedCdcEvent> {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractExtractedCdcEventListener.class);
    public static final String MYSQL = "mysql";
    public static final String ORACLE = "oracle";
    public static final String CONNECTOR = "connector";

    @Async
    @Override
    public void onApplicationEvent(ExtractedCdcEvent event) {

        Map<String, Object> sourceMap = event.getCdcEventBo().getTargetSource();
        String identifyKey;
        String schema;
        //针对mysql CDC event 处理
        if (MYSQL.equals(sourceMap.get(CONNECTOR))){
            identifyKey = Joiner.on("|").skipNulls()
                    .join(
                            sourceMap.get("file"),
                            sourceMap.get("pos"),
                            StrUtil.removeAll((String)sourceMap.get("gtid"), CharUtil.DASHED)
                    );
            schema = DbConstants.NULL;
        }
        //针对oracle CDC event 处理
        else if (ORACLE.equals(sourceMap.get(CONNECTOR))){
            identifyKey = Joiner.on("|").skipNulls()
                    .join(
                            sourceMap.get("scn"),
                            sourceMap.get("txId")
                    );
            schema = (String) sourceMap.get("schema");
        }
        else {
            throw new UnsupportedOperationException("not support CDC event for the DB type: "+sourceMap.get(CONNECTOR));
        }
        //设置组合后的Event唯一标识key
        event.getCdcEventBo().setIdentifyKey(identifyKey);
        event.getCdcEventBo().setDb((String) sourceMap.get("db"));
        event.getCdcEventBo().setTable((String) sourceMap.get("table"));
        event.getCdcEventBo().setSchema(schema);

        LOGGER.debug("CDC Event: {}", event);

        //构造CommonWithHeaderRequest为后续Flow封装业务对象
        DbzRequestHeader dbzRequestHeader = new DbzRequestHeader();
        //用连接器逻辑名称作为渠道Id
        dbzRequestHeader.setChannelId((String) sourceMap.get("name"));
        dbzRequestHeader.setTxnSerialNo(identifyKey);
        dbzRequestHeader.setTimestamp(new Date(event.getCdcEventBo().getProcessTime()));
        CommonWithHeaderRequest<DbzRequestHeader, ExtractedCdcEventBo> request =
                new CommonWithHeaderRequestBuilder<DbzRequestHeader, ExtractedCdcEventBo>()
                        .build()
                        .setRequestHead(dbzRequestHeader)
                        .setRequestData(event.getCdcEventBo())
                ;

        eventDispatcher(request);

    }

    /**
     * 事件Listener的回调业务逻辑，用于下游业务服务分发相应的 Trans Flow
     * @param request 封装后的事件，传递给业务逻辑
     */
    public abstract void eventDispatcher(CommonWithHeaderRequest<DbzRequestHeader, ExtractedCdcEventBo> request);
}
