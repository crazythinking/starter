package net.engining.transflow.autoconfigure.autotest.support;

import net.engining.control.api.key.ChannelKey;
import net.engining.control.api.key.ChannelRequestSeqKey;
import net.engining.control.api.key.OnlineDataKey;
import net.engining.control.sdk.AbstractFlowTransPayload;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-10-26 18:00
 * @since :
 **/
public class SampleFlowRequest extends AbstractFlowTransPayload {

    /**
     * FlowCode通过代码生成插件默认赋值Flow的类名
     *
     */
    public SampleFlowRequest() {
        super("sample");
    }

    /*
     * 请求报文体
     */
    public String getOnlineData() {
        return (String)dataMap.get(OnlineDataKey.class);
    }

    /*
     * 请求报文体
     */
    public void setOnlineData(String value) {
        dataMap.put(OnlineDataKey.class, value);
    }

    /*
     * 渠道流水号
     */
    public String getChannelRequestSeq() {
        return (String)dataMap.get(ChannelRequestSeqKey.class);
    }

    /*
     * 渠道流水号
     */
    public void setChannelRequestSeq(String value) {
        dataMap.put(ChannelRequestSeqKey.class, value);
    }

    /*
     * 渠道ID（请求系统标识）
     */
    public String getChannel() {
        return (String)dataMap.get(ChannelKey.class);
    }

    /*
     * 渠道ID（请求系统标识）
     */
    public void setChannel(String value) {
        dataMap.put(ChannelKey.class, value);
    }

    public Integer getIntValue1() {
        return (Integer)dataMap.get(IntValue1Key.class);
    }

    public void setIntValue1(Integer value) {
        dataMap.put(IntValue1Key.class, value);
    }

    public Integer getIntValue2() {
        return (Integer)dataMap.get(IntValue2Key.class);
    }

    public void setIntValue2(Integer value) {
        dataMap.put(IntValue2Key.class, value);
    }

}
