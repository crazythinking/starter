package net.engining.bustream.base;


import org.springframework.context.ApplicationEvent;

/**
 * 用于接受ErrorMessage的时间载体，便于业务端基于ApplicationEvent扩展使用；
 * 业务端自行扩展相应的Listener；
 *
 * @author : Eric Lu
 * @version :
 * @date : 2023-02-27 15:39
 * @since :
 **/
public class ErrorMessageEvent extends ApplicationEvent {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public ErrorMessageEvent(Object source) {
        super(source);
    }

}
