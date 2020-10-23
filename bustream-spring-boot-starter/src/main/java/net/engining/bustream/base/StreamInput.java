package net.engining.bustream.base;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * 自定义的 Stream Channel，需要通过@EnableBinding启用
 *
 * @author Eric Lu
 * @date 2020-10-22 11:12
 **/
public interface StreamInput {

    String INPUT = "msgInput";

    @Input(StreamInput.INPUT)
    SubscribableChannel msgInput();
}
