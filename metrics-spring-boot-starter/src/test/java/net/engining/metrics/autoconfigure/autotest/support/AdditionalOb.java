package net.engining.metrics.autoconfigure.autotest.support;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * @author Eric Lu
 * @create 2019-08-06 14:06
 **/
public class AdditionalOb implements Serializable {

    private String aa;

    public String getAa() {
        return aa;
    }

    public void setAa(String aa) {
        this.aa = aa;
    }

    public static void main(String[] args) {
        AdditionalOb ob = new AdditionalOb();
        ob.setAa("bbb");
        System.out.println(JSON.toJSONString(ob));
    }
}
