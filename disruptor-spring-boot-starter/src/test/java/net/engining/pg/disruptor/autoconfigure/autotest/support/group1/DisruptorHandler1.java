package net.engining.pg.disruptor.autoconfigure.autotest.support.group1;

import cn.hutool.core.util.StrUtil;
import net.engining.pg.disruptor.event.DisruptorBizDataEvent;
import net.engining.pg.disruptor.event.handler.AbstractParallelGroupedEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-03-16 17:13
 * @since :
 **/
public class DisruptorHandler1 extends AbstractParallelGroupedEventHandler<DisruptorBizDataEvent<String>> {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(DisruptorHandler1.class);

    /**
     * 构造函数
     *
     * @param groupName
     * @param batchSize 每个批次应包含的Event数量
     */
    public DisruptorHandler1(String groupName, int batchSize) {
        super(groupName, batchSize);
    }

    @Override
    protected void doHandlerInternal(DisruptorBizDataEvent<String> event) throws Exception {
        LOGGER.info(
                "disruptor event ({})",
                event.toString()+ StrUtil.COMMA + " bizData :" + event.getBizData()
        );
    }

    @Override
    protected void doHandlerInternal(List<DisruptorBizDataEvent<String>> eventBuffer) throws Exception {
        throw new UnsupportedOperationException("not support batch processing");
    }

    @Override
    public boolean isEnabled(DisruptorBizDataEvent<String> event) {
        return "en".equals(event.getTag());
    }
}
