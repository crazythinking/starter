package net.engining.bustream.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionBlockedEvent;
import org.springframework.context.ApplicationListener;

/**
 * RabbitMQ ConnectionBlockedEvent 监听器
 *
 * @author : Eric Lu
 * @version :
 * @since : 2024-04-19 9:15
 **/
public class ConnectionBlockedEventListener implements ApplicationListener<ConnectionBlockedEvent>{
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionBlockedEventListener.class);

    @Override
    public void onApplicationEvent(ConnectionBlockedEvent event) {
        LOGGER.warn("RabbitMQ connection is blocked, reason: {}", event.getReason());
    }
}
