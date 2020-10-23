package net.engining.bustream.base;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * 自定义的 Stream Channel，需要通过@EnableBinding启用
 *
 * @author Eric Lu
 * @date 2020-10-22 11:15
 **/
public interface StreamOutput {

    String OUTPUT = "msgOutput";

    @Output(StreamOutput.OUTPUT)
    MessageChannel msgOutput();
}
