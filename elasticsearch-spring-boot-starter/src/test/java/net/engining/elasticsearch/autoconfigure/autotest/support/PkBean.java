package net.engining.elasticsearch.autoconfigure.autotest.support;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-09-08 14:30
 * @since :
 **/
public class PkBean {

    private Long aLong;

    private String strId;

    public PkBean(Long aLong, String strId) {
        this.aLong = aLong;
        this.strId = strId;
    }

    public Long getaLong() {
        return aLong;
    }

    public void setaLong(Long aLong) {
        this.aLong = aLong;
    }

    public String getStrId() {
        return strId;
    }

    public void setStrId(String strId) {
        this.strId = strId;
    }
}
