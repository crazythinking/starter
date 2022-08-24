package net.engining.sftp.autoconfigure.autotest.support;

import cn.hutool.core.util.StrUtil;
import net.engining.pg.disruptor.event.DisruptorBizDataEvent;
import net.engining.pg.disruptor.event.handler.AbstractSerialChainGroupedEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-03-16 17:13
 * @since :
 **/
public class SftpTest1FileDisruptorHandler01
        extends AbstractSerialChainGroupedEventHandler<DisruptorBizDataEvent<File>>{
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(SftpTest1FileDisruptorHandler01.class);

    private static final int ORDER = 1;

    public SftpTest1FileDisruptorHandler01(String groupName, int batchSize) {
        super(groupName, batchSize);
        super.order = ORDER;
    }

    @Override
    protected void doHandlerInternal(DisruptorBizDataEvent<File> event) throws Exception {
        LOGGER.warn(
                "disruptor event ({})",
                event.toString()+ StrUtil.COMMA + " bizData :" +event.getBizData().toString()
        );
        Thread.sleep(1000);
    }

    @Override
    protected void doHandlerInternal(List<DisruptorBizDataEvent<File>> eventBuffer) throws Exception {
        throw new UnsupportedOperationException("not support batch processing");
    }

    @Override
    public boolean isEnabled(DisruptorBizDataEvent<File> event) {
        return true;
    }
}

