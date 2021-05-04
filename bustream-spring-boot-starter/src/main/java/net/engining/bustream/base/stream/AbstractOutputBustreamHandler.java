package net.engining.bustream.base.stream;

import net.engining.bustream.base.BustreamHandler;
import net.engining.pg.support.utils.ExceptionUtilsExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

import java.io.Serializable;
import java.util.Map;


/**
 * 默认消息生产者的处理抽象类，由业务类继承并注入 Spring Context；
 * 注意：此类绑定了Channel -> OUTPUT；如果业务类需要自定义的Channel时，需要自行实现{@link AbstractProduceBustreamHandler}；
 *
 * @author Eric Lu
 * @date 2020-10-29 18:16
 **/
public abstract class AbstractOutputBustreamHandler<E extends Serializable> extends AbstractProduceBustreamHandler<E> {

    @Autowired
    @Qualifier(Source.OUTPUT)
    @Override
    protected void setMessageChannel(MessageChannel messageChannel) {
        super.setMessageChannel(messageChannel);
    }

}
