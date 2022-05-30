package net.engining.sftp.autoconfigure.props;

/**
 * SFTP配置项
 *
 * @author Eric Lu
 * @create 2019-06-07 13:38
 **/
public class SftpProperties {
    /**
     * 地址
     */
    private String host;
    /**
     * 端口
     */
    private int port = 22;
    /**
     * 用户名
     */
    private String user;

    /**
     * 密码
     */
    private String password = "";
    /**
     * 编码
     */
    private String charset = "UTF-8";

    /**
     * The timeout property is used as the socket timeout parameter, as well as
     * the default connection timeout. Defaults to <code>60000</code>, which means,
     * that no timeout will occur.
     */
    private int timeout = 60000;

    /**
     * Specifies the number of server-alive messages, which will be sent without
     * any reply from the server before disconnecting. If not set, this property
     * defaults to <code>1</code>.
     * 长连接时使用的参数
     */
    private int serverAliveCountMax = 1;

    /**
     * The maximum cache size
     */
    private int sessionCacheSize = 10;

    /**
     * Sets the limit of how long to wait for a session to become available.
     *
     */
    private int cachingSessionWaitTimeout = 60000;

    /**
     * 默认的远程同步目录
     */
    private String defaultRemoteDirectory;

    /**
     * 远程文件保存到本地的默认目录
     */
    private String defaultLocalDirectory = "./sftpinput/";

    /**
     * Should we delete the remote source files after copying to the local directory? By default this is false.
     */
    private boolean deleteRemoteFiles;

    /**
     * 是否启用同步器
     */
    private boolean syncEnabled = false;

    /**
     * 需同步文件的其名称的正则表达式
     */
    private String syncFileNameRegex;

    /**
     * 同步器每次轮询抓取文件最大数量
     */
    private int maxFetchSize = 10;

    /**
     * 同步器轮询间隔毫秒数
     */
    private long pollingInterval = 1000;

    /**
     * 同步器每次轮询发出的最大消息（文件）数量
     */
    private int maxMessagesPerPoll = 10;

    /**
     * 是否启用ChannelExec
     */
    private boolean execEnabled = false;

    public boolean isExecEnabled() {
        return execEnabled;
    }

    public void setExecEnabled(boolean execEnabled) {
        this.execEnabled = execEnabled;
    }

    public boolean isSyncEnabled() {
        return syncEnabled;
    }

    public void setSyncEnabled(boolean syncEnabled) {
        this.syncEnabled = syncEnabled;
    }

    public int getMaxMessagesPerPoll() {
        return maxMessagesPerPoll;
    }

    public void setMaxMessagesPerPoll(int maxMessagesPerPoll) {
        this.maxMessagesPerPoll = maxMessagesPerPoll;
    }

    public boolean isDeleteRemoteFiles() {
        return deleteRemoteFiles;
    }

    public void setDeleteRemoteFiles(boolean deleteRemoteFiles) {
        this.deleteRemoteFiles = deleteRemoteFiles;
    }

    public String getSyncFileNameRegex() {
        return syncFileNameRegex;
    }

    public void setSyncFileNameRegex(String syncFileNameRegex) {
        this.syncFileNameRegex = syncFileNameRegex;
    }

    public int getMaxFetchSize() {
        return maxFetchSize;
    }

    public void setMaxFetchSize(int maxFetchSize) {
        this.maxFetchSize = maxFetchSize;
    }

    public long getPollingInterval() {
        return pollingInterval;
    }

    public void setPollingInterval(long pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    public int getSessionCacheSize() {
        return sessionCacheSize;
    }

    public void setSessionCacheSize(int sessionCacheSize) {
        this.sessionCacheSize = sessionCacheSize;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getDefaultRemoteDirectory() {
        return defaultRemoteDirectory;
    }

    public void setDefaultRemoteDirectory(String defaultRemoteDirectory) {
        this.defaultRemoteDirectory = defaultRemoteDirectory;
    }

    public String getDefaultLocalDirectory() {
        return defaultLocalDirectory;
    }

    public void setDefaultLocalDirectory(String defaultLocalDirectory) {
        this.defaultLocalDirectory = defaultLocalDirectory;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getServerAliveCountMax() {
        return serverAliveCountMax;
    }

    public void setServerAliveCountMax(int serverAliveCountMax) {
        this.serverAliveCountMax = serverAliveCountMax;
    }

    public int getCachingSessionWaitTimeout() {
        return cachingSessionWaitTimeout;
    }

    public void setCachingSessionWaitTimeout(int cachingSessionWaitTimeout) {
        this.cachingSessionWaitTimeout = cachingSessionWaitTimeout;
    }

}
