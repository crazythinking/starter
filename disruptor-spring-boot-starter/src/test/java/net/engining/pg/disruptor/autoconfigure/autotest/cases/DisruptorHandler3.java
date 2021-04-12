package net.engining.pg.disruptor.autoconfigure.autotest.cases;

import cn.hutool.core.util.StrUtil;
import net.engining.pg.disruptor.event.DisruptorBizDataEvent;
import net.engining.pg.disruptor.event.handler.AbstractParallelGroupedEventHandler;
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
public class DisruptorHandler3 extends AbstractParallelGroupedEventHandler<DisruptorBizDataEvent<String>>
                                                                                        implements InitializingBean {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(DisruptorHandler3.class);

    @Override
    public void setEnabled(DisruptorBizDataEvent<String> event) {
        if ("en".equals(event.getTag())){
            this.enabled = true;
        }
    }

    @Override
    protected void doHandlerInternal(DisruptorBizDataEvent<String> event) throws Exception {
        LOGGER.info(
                "disruptor event ({})",
                event.toString()+ StrUtil.COMMA + " bizData :" +event.getBizData()
        );
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.groupName = "TestCase-Event1";
    }
}
