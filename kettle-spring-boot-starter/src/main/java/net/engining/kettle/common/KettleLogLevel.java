package net.engining.kettle.common;

import org.pentaho.di.core.logging.LogLevel;

/**
 * kettle 日志级别
 *
 * @author 陈宝
 * @version 1.0
 * @date 2020/9/23 14:44
 * @since 1.0
 */
public enum KettleLogLevel {
    /**
     * 基础
     */
    basic(LogLevel.BASIC),
    /**
     * 详情
     */
    detail(LogLevel.DETAILED),
    /**
     * 错误
     */
    error(LogLevel.ERROR),
    /**
     * 调试
     */
    debug(LogLevel.DEBUG),
    /**
     * 最小的
     */
    minimal(LogLevel.MINIMAL),
    /**
     * 行级
     */
    rowlevel(LogLevel.ROWLEVEL),
    /**
     * 没有
     */
    nothing(LogLevel.NOTHING);

    private LogLevel logLevel;
    KettleLogLevel(LogLevel logLevel){
        this.logLevel=logLevel;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }
}
