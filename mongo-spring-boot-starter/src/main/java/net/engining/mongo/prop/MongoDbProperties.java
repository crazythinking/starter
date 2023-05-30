package net.engining.mongo.prop;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2023-05-25 13:03
 * @since :
 **/
@ConfigurationProperties(prefix = "pg.mongo")
public class MongoDbProperties implements InitializingBean {

    private boolean enabled = true;

    private ConnectionPool connectionPool = new ConnectionPool();

    private Socket socket = new Socket();

    private Cluster cluster = new Cluster();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }

    public void setConnectionPool(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    public static class Cluster {
        /**
         * 服务器选择超时时间,单位毫秒
         */
        private long serverSelectionTimeout = 500;

        public long getServerSelectionTimeout() {
            return serverSelectionTimeout;
        }

        public void setServerSelectionTimeout(long serverSelectionTimeout) {
            this.serverSelectionTimeout = serverSelectionTimeout;
        }
    }

    public static class Socket {
        /**
         * 连接超时时间,单位毫秒
         */
        private int connectTimeout = 3000;

        /**
         * 读取超时时间,单位毫秒
         */
        private int readTimeout = 1000;

        public int getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public int getReadTimeout() {
            return readTimeout;
        }

        public void setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
        }

    }

    /** ConnectionPoolSettings */
    public static class ConnectionPool {
        private int maxSize = 10;
        private int minSize = 1;
        private long maxWaitTime = 3000;

        /**
         * 连接最大存活时间,单位毫秒
         */
        private long maxConnectionLifeTime = 1000*60*10;

        /**
         * 连接最大空闲时间,单位毫秒
         */
        private long maxConnectionIdleTime = 1000*60;

        /**
         * 维护线程初始延迟时间,单位毫秒
         */
        private long maintenanceInitialDelay = 10000;

        /**
         * 维护线程频率,单位毫秒
         */
        private long maintenanceFrequency = 60000;

        public int getMaxSize() {
            return maxSize;
        }

        public void setMaxSize(int maxSize) {
            this.maxSize = maxSize;
        }

        public int getMinSize() {
            return minSize;
        }

        public void setMinSize(int minSize) {
            this.minSize = minSize;
        }

        public long getMaxWaitTime() {
            return maxWaitTime;
        }

        public void setMaxWaitTime(long maxWaitTime) {
            this.maxWaitTime = maxWaitTime;
        }

        public long getMaxConnectionLifeTime() {
            return maxConnectionLifeTime;
        }

        public void setMaxConnectionLifeTime(long maxConnectionLifeTime) {
            this.maxConnectionLifeTime = maxConnectionLifeTime;
        }

        public long getMaxConnectionIdleTime() {
            return maxConnectionIdleTime;
        }

        public void setMaxConnectionIdleTime(long maxConnectionIdleTime) {
            this.maxConnectionIdleTime = maxConnectionIdleTime;
        }

        public long getMaintenanceInitialDelay() {
            return maintenanceInitialDelay;
        }

        public void setMaintenanceInitialDelay(long maintenanceInitialDelay) {
            this.maintenanceInitialDelay = maintenanceInitialDelay;
        }

        public long getMaintenanceFrequency() {
            return maintenanceFrequency;
        }

        public void setMaintenanceFrequency(long maintenanceFrequency) {
            this.maintenanceFrequency = maintenanceFrequency;
        }
    }




}
