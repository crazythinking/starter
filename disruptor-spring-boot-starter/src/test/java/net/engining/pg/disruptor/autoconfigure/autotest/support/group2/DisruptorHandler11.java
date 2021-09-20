package net.engining.pg.disruptor.autoconfigure.autotest.support.group2;

import cn.hutool.core.util.StrUtil;
import net.engining.pg.disruptor.event.DisruptorBizDataEvent;
import net.engining.pg.disruptor.event.handler.AbstractSerialChainGroupedEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-03-16 17:13
 * @since :
 **/
public class DisruptorHandler11 extends AbstractSerialChainGroupedEventHandler<DisruptorBizDataEvent<Integer>>{
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(DisruptorHandler11.class);

    private static final int ORDER = 3;

    public DisruptorHandler11(String groupName, int batchSize) {
        super(groupName, batchSize);
        super.order = ORDER;
    }

    @Override
    protected void doHandlerInternal(DisruptorBizDataEvent<Integer> event) throws Exception {
        LOGGER.info(
                "disruptor event ({}), result={}",
                event.toString()+ StrUtil.COMMA + " bizData :" +event.getBizData().toString(),
                event.getBizData()+ORDER
        );
        Thread.sleep(1000);
    }

    @Override
    protected void doHandlerInternal(List<DisruptorBizDataEvent<Integer>> eventBuffer) throws Exception {

    }

    @Override
    public boolean isEnabled(DisruptorBizDataEvent<Integer> event) {
        return "en".equals(event.getTag());
    }
}
