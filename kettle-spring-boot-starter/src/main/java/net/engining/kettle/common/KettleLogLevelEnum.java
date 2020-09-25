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
public enum KettleLogLevelEnum {
    /**
     * 基础
     */
    BASIC(LogLevel.BASIC),
    /**
     * 详情
     */
    DETAIL(LogLevel.DETAILED),
    /**
     * 错误
     */
    ERROR(LogLevel.ERROR),
    /**
     * 调试
     */
    DEBUG(LogLevel.DEBUG),
    /**
     * 最小的
     */
    MINIMAL(LogLevel.MINIMAL),
    /**
     * 行级
     */
    ROWLEVEL(LogLevel.ROWLEVEL),
    /**
     * 没有
     */
    NOTHING(LogLevel.NOTHING);

    private LogLevel logLevel;
    KettleLogLevelEnum(LogLevel logLevel){
        this.logLevel=logLevel;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }
}
