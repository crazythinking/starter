package net.engining.bustream.base.stream;

import org.springframework.cloud.stream.messaging.Processor;

/**
 * 自定义的 Stream Channel，需要通过@EnableBinding启用;
 * 继承Processor定义的input和output channel, 添加pollableInput;
 *
 * @author Eric Lu
 * @date 2020-09-23 19:34
 **/
public interface StreamInOutChannel extends StreamPollableInput, Processor {

}
