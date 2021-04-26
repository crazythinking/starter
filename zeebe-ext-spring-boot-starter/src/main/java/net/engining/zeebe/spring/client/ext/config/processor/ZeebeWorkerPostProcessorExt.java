package net.engining.zeebe.spring.client.ext.config.processor;

import io.zeebe.spring.client.bean.value.factory.ReadZeebeWorkerValue;
import io.zeebe.spring.client.config.processor.ZeebeWorkerPostProcessor;

/**
 * 扩展
 * @author : Eric Lu
 * @version :
 * @date : 2021-04-25 17:26
 * @since :
 **/
public class ZeebeWorkerPostProcessorExt extends ZeebeWorkerPostProcessor {

    public ZeebeWorkerPostProcessorExt(ReadZeebeWorkerValue reader) {
        super(reader);
    }
}
