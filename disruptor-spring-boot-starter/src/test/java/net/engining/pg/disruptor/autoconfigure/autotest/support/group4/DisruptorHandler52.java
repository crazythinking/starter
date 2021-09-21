package net.engining.pg.disruptor.autoconfigure.autotest.support.group4;

import cn.hutool.core.util.StrUtil;
import net.engining.pg.disruptor.event.DisruptorBizDataEvent;
import net.engining.pg.disruptor.event.handler.AbstractMultiSerialChainGroupedEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-03-16 17:13
 * @since :
 **/
public class DisruptorHandler52 extends AbstractMultiSerialChainGroupedEventHandler<DisruptorBizDataEvent<Integer>> {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(DisruptorHandler52.class);

    private static final int ORDER = 2;

    public DisruptorHandler52(String groupName, int listIndex, int batchSize) {
        super(groupName, listIndex, batchSize);
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
        throw new UnsupportedOperationException("unsupported batch events handler");
    }

    @Override
    public boolean isEnabled(DisruptorBizDataEvent<Integer> event) {
        return "en".equals(event.getTag());
    }
}
