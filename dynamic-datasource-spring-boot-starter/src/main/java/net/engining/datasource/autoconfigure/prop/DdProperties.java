package net.engining.datasource.autoconfigure.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 动态数据源相关通用配置项
 *
 * @author : Eric Lu
 * @version :
 * @date : 2021-10-20 14:14
 * @since :
 **/
@ConfigurationProperties(prefix = "pg.datasource.dynamic")
public class DdProperties {

    /**
     * 异步线程池的核心线程数大小
     */
    private int asyncExcutorColePoolSize = 5;

    /**
     * 异步线程池的最大线程数大小
     */
    private int asyncExcutorMaxPoolSize = 5;

    /**
     * 异步线程池的队列大小
     */
    private int asyncExcutorQueueCapacity = 10;

    public int getAsyncExcutorColePoolSize() {
        return asyncExcutorColePoolSize;
    }

    public void setAsyncExcutorColePoolSize(int asyncExcutorColePoolSize) {
        this.asyncExcutorColePoolSize = asyncExcutorColePoolSize;
    }

    public int getAsyncExcutorMaxPoolSize() {
        return asyncExcutorMaxPoolSize;
    }

    public void setAsyncExcutorMaxPoolSize(int asyncExcutorMaxPoolSize) {
        this.asyncExcutorMaxPoolSize = asyncExcutorMaxPoolSize;
    }

    public int getAsyncExcutorQueueCapacity() {
        return asyncExcutorQueueCapacity;
    }

    public void setAsyncExcutorQueueCapacity(int asyncExcutorQueueCapacity) {
        this.asyncExcutorQueueCapacity = asyncExcutorQueueCapacity;
    }
}
