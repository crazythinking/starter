package net.engining.pg.disruptor.autoconfigure.autotest.cases;

import cn.hutool.core.util.StrUtil;
import net.engining.pg.disruptor.event.DisruptorBizDataEvent;
import net.engining.pg.disruptor.event.handler.AbstractDependenciesDiamondGroupedEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-03-16 17:13
 * @since :
 **/
@Component
public class DisruptorHandler31 extends AbstractDependenciesDiamondGroupedEventHandler<DisruptorBizDataEvent<Integer>>
                                                                                        implements InitializingBean {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(DisruptorHandler31.class);

    private static final int ORDER = 3;

    public DisruptorHandler31() {
        super(1);
    }

    @Override
    public void setEnabled(DisruptorBizDataEvent<Integer> event) {
        if ("en".equals(event.getTag())){
            this.enabled = true;
        }
    }

    @Override
    protected void doHandlerInternal(DisruptorBizDataEvent<Integer> event, long sequence, boolean endOfBatch) throws Exception {
        LOGGER.info(
                "disruptor event ({}) execution on sequence={}, endOfBatch={}, result={}",
                event.toString()+ StrUtil.COMMA + " bizData :" +event.getBizData().toString(),
                sequence,
                endOfBatch,
                event.getBizData()+ORDER
        );
        Thread.sleep(1000);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.groupName = "TestCase-Event3";
        super.order = ORDER;
    }
}
