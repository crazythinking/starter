package net.engining.kettle.common;

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
    basic,
    /**
     * 详情
     */
    detail,
    /**
     * 错误
     */
    error,
    /**
     * 调试
     */
    debug,
    /**
     * 最小的
     */
    minimal,
    /**
     * 行级
     */
    rowlevel,
    /**
     * 没有
     */
    nothing;
}
