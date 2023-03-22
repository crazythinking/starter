package net.engining.bustream.base;

import net.engining.pg.support.utils.ValidateUtilExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.integration.amqp.support.NackedAmqpMessageException;
import org.springframework.integration.amqp.support.ReturnedAmqpMessageException;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.ErrorMessage;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2023-02-28 9:34
 * @since :
 **/
public class ErrorMessageEventListener implements ApplicationListener<ErrorMessageEvent> {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorMessageEventListener.class);

    @Override
    public void onApplicationEvent(ErrorMessageEvent event) {
        ErrorMessage errorMessage = (ErrorMessage) event.getSource();
        String loggerPattern;
        String messageId = null;
        String error;
        //TODO 以下实现了Amqp相关，需要针对Kafka，RocketMq等扩展
        if (errorMessage.getPayload() instanceof NackedAmqpMessageException) {
            NackedAmqpMessageException nackedAmqpMessageException = (NackedAmqpMessageException) errorMessage.getPayload();
            loggerPattern = "Producer sent message({}) NAck error: {}";
            if (ValidateUtilExt.isNotNullOrEmpty(nackedAmqpMessageException.getFailedMessage())){
                messageId = (String) nackedAmqpMessageException.getFailedMessage()
                        .getHeaders().get(BustreamHandler.ORIGIN_MESSAGE_ID);
            }
            error = nackedAmqpMessageException.getNackReason();
            LOGGER.error(loggerPattern, messageId, error);
            return;
        }
        if (errorMessage.getPayload() instanceof ReturnedAmqpMessageException) {
            ReturnedAmqpMessageException returnedAmqpMessageException = (ReturnedAmqpMessageException) errorMessage.getPayload();
            loggerPattern = "Producer sent message({}) returned error: {}";
            messageId = (String) returnedAmqpMessageException
                    .getAmqpMessage().getMessageProperties().getHeaders().get(BustreamHandler.ORIGIN_MESSAGE_ID);
            error =  returnedAmqpMessageException.getReplyText();
            LOGGER.error(loggerPattern, messageId, error);
            return;
        }
        if (errorMessage.getPayload() instanceof MessagingException) {
            loggerPattern = "Consumer handling message({}) error: {}";
            MessagingException messagingException = (MessagingException) errorMessage.getPayload();
            if (ValidateUtilExt.isNotNullOrEmpty(messagingException.getFailedMessage())){
                messageId = (String) messagingException.getFailedMessage().getHeaders().get(BustreamHandler.ORIGIN_MESSAGE_ID);
            }
            error =  messagingException.getMessage();
            LOGGER.error(loggerPattern, messageId, error);
            return;
        }
        if(errorMessage.getPayload() instanceof Exception) {
            loggerPattern = "message({}) happened some error: {}";
            if (ValidateUtilExt.isNotNullOrEmpty(errorMessage.getOriginalMessage())) {
                Message<?> message = errorMessage.getOriginalMessage();
                messageId = (String) message.getHeaders().get(BustreamHandler.ORIGIN_MESSAGE_ID);
            }
            error = errorMessage.getPayload().getMessage();
            LOGGER.error(loggerPattern, messageId, error);
        }
    }
}
