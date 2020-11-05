package net.engining.bustream.config;

import net.engining.bustream.base.stream.StreamPollableInput;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 *
 * @author : Eric Lu
 * @version :
 * @date : 2020-10-29 14:20
 * @since :
 **/
@Profile("stream.common.bindings.pollinput")
@EnableBinding({
        StreamPollableInput.class
})
@PropertySource("classpath:application-stream.common.bindings.pollinput.yml")
public class StreamCommonBindingsPollinputConfig {
}
