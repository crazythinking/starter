package net.engining.debezium.event;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-08-19 13:52
 * @since :
 **/
public class DbzRequestHeader implements Serializable {

    /**
     * 渠道Id（请求系统标识）
     */
    private String channelId;

    /**
     * 请求交易流水号
     */
    private String txnSerialNo;

    /**
     * 交易请求时间
     */
    private Date timestamp;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getTxnSerialNo() {
        return txnSerialNo;
    }

    public void setTxnSerialNo(String txnSerialNo) {
        this.txnSerialNo = txnSerialNo;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "DbzRequestHeader{" +
                "channelId='" + channelId + '\'' +
                ", txnSerialNo='" + txnSerialNo + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
