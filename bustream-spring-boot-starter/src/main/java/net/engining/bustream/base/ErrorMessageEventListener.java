package net.engining.bustream.base;

import net.engining.pg.support.utils.ValidateUtilExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
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

    protected void customErrorHandle(ErrorMessage errorMessage) {
        String loggerPattern;
        String messageId = null;
        String error;
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

    @Override
    public void onApplicationEvent(ErrorMessageEvent event) {
        ErrorMessage errorMessage = (ErrorMessage) event.getSource();
        customErrorHandle(errorMessage);
    }
}
