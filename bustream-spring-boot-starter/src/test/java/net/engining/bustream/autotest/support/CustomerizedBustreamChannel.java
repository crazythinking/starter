package net.engining.bustream.autotest.support;

import net.engining.bustream.base.stream.StreamPollableInput;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.binder.PollableMessageSource;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author Eric Lu
 * @date 2021-05-06 9:48
 **/
public interface CustomerizedBustreamChannel {

    String TEST1_INPUT = "test1Input";

    String TEST1_OUTPUT = "test1Output";

    String TEST1_POLLINPUT = "test1PollableInput";

    @Input(StreamPollableInput.POLLINPUT)
    PollableMessageSource pollableInput();

    @Output(TEST1_OUTPUT)
    MessageChannel output();

    @Input(TEST1_INPUT)
    SubscribableChannel input();
}
