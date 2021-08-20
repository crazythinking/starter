package net.engining.bustream.base.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.binder.PollableMessageSource;

/**
 * 自定义的 Stream Channel，需要通过@EnableBinding启用
 *
 * @author Eric Lu
 * @date 2020-10-22 11:16
 **/
public interface StreamPollableInput {

    String POLLINPUT = "pollableInput";

    /**
     * 轮询的Input消息通道
     * @return PollableMessageSource
     */
    @Input(StreamPollableInput.POLLINPUT)
    PollableMessageSource pollableInput();
}
