/* Drop Tables */
DROP TABLE IF EXISTS OPER_ADT_LOG;

/* Create Tables */

-- 操作审计日志表
CREATE TABLE OPER_ADT_LOG
(
    ID INT COMMENT 'ID',
    LOGIN_ID String COMMENT '用户登录ID',
    REQUEST_URI String COMMENT '请求交易URI',
    REQUEST_BODY String COMMENT '请求报文',
    OPER_TIME DateTime COMMENT '操作时间',
    JPA_VERSION INT COMMENT '版本号'
)
    ENGINE = MergeTree()
        PRIMARY KEY (ID)
        ORDER BY (ID)
;



