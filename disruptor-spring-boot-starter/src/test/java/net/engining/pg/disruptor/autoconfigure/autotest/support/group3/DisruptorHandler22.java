package net.engining.pg.disruptor.autoconfigure.autotest.support.group3;

import cn.hutool.core.util.StrUtil;
import net.engining.pg.disruptor.event.DisruptorBizDataEvent;
import net.engining.pg.disruptor.event.handler.AbstractDiamondGroupedEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-03-16 17:13
 * @since :
 **/
public class DisruptorHandler22 extends AbstractDiamondGroupedEventHandler<DisruptorBizDataEvent<Integer>> {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(DisruptorHandler22.class);

    public DisruptorHandler22(String groupName, int listIndex, int batchSize) {
        super(groupName, listIndex, batchSize);
    }

    @Override
    protected void doHandlerInternal(DisruptorBizDataEvent<Integer> event) throws Exception {
        LOGGER.info(
                "disruptor event ({}), result={}",
                event.toString()+ StrUtil.COMMA + " bizData :" +event.getBizData().toString(),
                event.getBizData()+super.getListIndex()
        );
        Thread.sleep(1000);
    }

    @Override
    protected void doHandlerInternal(List<DisruptorBizDataEvent<Integer>> eventBuffer) throws Exception {
        LOGGER.info(
                "disruptor ({}) events",
                eventBuffer.size()
        );
    }

    @Override
    public boolean isEnabled(DisruptorBizDataEvent<Integer> event) {
        return "en".equals(event.getTag());
    }
}
