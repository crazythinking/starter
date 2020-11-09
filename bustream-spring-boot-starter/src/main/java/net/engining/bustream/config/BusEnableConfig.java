package net.engining.bustream.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.bus.SpringCloudBusClient;
import org.springframework.cloud.bus.event.AckRemoteApplicationEvent;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;

/**
 *
 * @author : Eric Lu
 * @version :
 * @date : 2020-10-29 14:20
 * @since :
 **/
@Profile("bus.enable")
@EnableBinding({
        SpringCloudBusClient.class
})
@PropertySource("classpath:application-bus.enable.yml")
public class BusEnableConfig {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(BusEnableConfig.class);

    @Autowired
    private ObjectMapper objectMapper;

    @EventListener
    public void onAckEvent(AckRemoteApplicationEvent event) throws JsonProcessingException {
        LOGGER.info(
                "Get one ACK message: {}",
                objectMapper.writeValueAsString(event)
        );
    }
}
