package net.engining.bustream.base;

import net.engining.pg.support.utils.ValidateUtilExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.amqp.support.NackedAmqpMessageException;
import org.springframework.integration.amqp.support.ReturnedAmqpMessageException;
import org.springframework.messaging.support.ErrorMessage;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2023-02-28 9:34
 * @since :
 **/
public class AmqpErrorMessageEventListener extends ErrorMessageEventListener {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(AmqpErrorMessageEventListener.class);

    @Override
    protected void customErrorHandle(ErrorMessage errorMessage) {
        String loggerPattern;
        String messageId = null;
        String error;
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
        }
    }

    @Override
    public void onApplicationEvent(ErrorMessageEvent event) {
        ErrorMessage errorMessage = (ErrorMessage) event.getSource();
        customErrorHandle(errorMessage);
        super.customErrorHandle(errorMessage);
    }
}
