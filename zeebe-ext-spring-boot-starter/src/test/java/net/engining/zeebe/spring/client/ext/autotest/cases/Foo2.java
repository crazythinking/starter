package net.engining.zeebe.spring.client.ext.autotest.cases;

import java.math.BigDecimal;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2019-12-26 10:32
 * @since :
 **/
public class Foo2 {
    String uid;
    String f1;
    BigDecimal f2;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getF1() {
        return f1;
    }

    public void setF1(String f1) {
        this.f1 = f1;
    }

    public BigDecimal getF2() {
        return f2;
    }

    public void setF2(BigDecimal f2) {
        this.f2 = f2;
    }

    @Override
    public String toString() {
        return "Foo2{" +
                "uid='" + uid + '\'' +
                ", f1='" + f1 + '\'' +
                ", f2=" + f2 +
                '}';
    }
}
