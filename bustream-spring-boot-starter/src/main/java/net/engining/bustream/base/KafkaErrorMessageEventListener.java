package net.engining.bustream.base;

import net.engining.pg.support.utils.ValidateUtilExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.kafka.support.KafkaSendFailureException;
import org.springframework.messaging.support.ErrorMessage;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2023-02-28 9:34
 * @since :
 **/
public class KafkaErrorMessageEventListener extends ErrorMessageEventListener {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaErrorMessageEventListener.class);

    @Override
    protected void customErrorHandle(ErrorMessage errorMessage) {
        String loggerPattern;
        String messageId = null;
        String error;
        if (errorMessage.getPayload() instanceof KafkaSendFailureException) {
            KafkaSendFailureException  kafkaSendFailureException = (KafkaSendFailureException ) errorMessage.getPayload();
            loggerPattern = "Producer sent message({}) NAck error: {}";
            if (ValidateUtilExt.isNotNullOrEmpty(kafkaSendFailureException.getFailedMessage())){
                messageId = (String) kafkaSendFailureException.getFailedMessage()
                        .getHeaders().get(BustreamHandler.ORIGIN_MESSAGE_ID);
            }
            error = kafkaSendFailureException.getMessage();
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
