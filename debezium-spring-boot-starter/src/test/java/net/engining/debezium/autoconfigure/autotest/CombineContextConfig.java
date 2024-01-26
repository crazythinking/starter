package net.engining.debezium.autoconfigure.autotest;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.engining.debezium.event.CdcConsumerFuncStreamHandler;
import net.engining.pg.support.core.context.ApplicationContextHolder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 通用Context配置
 *
 * @author Eric Lu
 */
@Configuration
@ComponentScan(
        basePackages = {
                "net.engining.debezium.autoconfigure.autotest.support"
        }
)
public class CombineContextConfig {

    /**
     * ApplicationContext的静态辅助Bean，建议项目必须注入
     */
    @Bean
    @Lazy(value = false)
    public ApplicationContextHolder applicationContextHolder() {
        return new ApplicationContextHolder();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    /**
     * 接收消息，无需显示调用，由监听器自动触发
     */
    @Bean
    public Consumer<Message<HashMap<String, Object>>> cdcXxlJobGroup(
            ObjectProvider<CdcConsumerFuncStreamHandler> streamHandler) {
        return message -> {
            try {
                Objects.requireNonNull(streamHandler.getIfAvailable()).consume(message.getPayload(), message.getHeaders());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Bean
    public Consumer<Message<HashMap<String, Object>>> cdcXxlJobLog(
            ObjectProvider<CdcConsumerFuncStreamHandler> streamHandler) {
        return message -> {
            try {
                Objects.requireNonNull(streamHandler.getIfAvailable()).consume(message.getPayload(), message.getHeaders());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

}
