package net.engining.transflow.autoconfigure.autotest.support;

import net.engining.control.sdk.AbstractFlowTransPayload;

/**
 * 用户开户服务
 */
public class SimpleFlowResponse extends AbstractFlowTransPayload {

    public SimpleFlowResponse() {
        super("sample");
    }

    public Integer getIntValue2() {
        return (Integer)dataMap.get(IntValue2Key.class);
    }

    public void setIntValue2(Integer value) {
        dataMap.put(IntValue2Key.class, value);
    }

}