package net.engining.zeebe.spring.client.ext.prop;

import java.time.Duration;

import static io.camunda.zeebe.spring.client.config.ZeebeClientSpringConfiguration.DEFAULT;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-05-31 11:16
 * @since :
 **/
public class EntityProperties {

    /**
     * 发送指令到Broker的超时时间
     */
    private Long requestTimeout = DEFAULT.getDefaultRequestTimeout().getSeconds();

    /**
     * 从Broker获取返回值的超时时间
     */
    private Long returnTimeout = Duration.ofSeconds(10).getSeconds();

    public Long getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Long requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public Long getReturnTimeout() {
        return returnTimeout;
    }

    public void setReturnTimeout(Long returnTimeout) {
        this.returnTimeout = returnTimeout;
    }

    @Override
    public String toString() {
        return "EntityProperties{" +
                "requestTimeout=" + requestTimeout +
                ", returnTimeout=" + returnTimeout +
                '}';
    }
}
