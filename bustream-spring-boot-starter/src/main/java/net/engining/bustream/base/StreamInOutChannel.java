package net.engining.bustream.base;

/**
 * 自定义的 Stream Channel，需要通过@EnableBinding启用
 *
 * @author Eric Lu
 * @date 2020-09-23 19:34
 **/
public interface StreamInOutChannel extends StreamInput, StreamPollableInput, StreamOutput {

}
