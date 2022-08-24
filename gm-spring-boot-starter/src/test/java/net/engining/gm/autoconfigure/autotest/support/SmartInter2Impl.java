package net.engining.gm.autoconfigure.autotest.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Service;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2022-08-23 12:16
 * @since :
 **/
@Service
public class SmartInter2Impl implements Inter2, SmartInitializingSingleton {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(SmartInter2Impl.class);

    static {
        testStatic();
    }

    private static void testStatic(){
        LOGGER.info("in testStatic()");
    }

    @Override
    public void test() {
        LOGGER.info("in test()");
    }

    @Override
    public void afterSingletonsInstantiated() {
        LOGGER.info("in afterSingletonsInstantiated()");
    }
}
