package net.engining.rocksdb.autoconfigure.autotest.support;

import net.engining.pg.support.core.exception.ErrorCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.keyvalue.annotation.KeySpace;

import java.io.Serializable;
import java.util.Date;
import java.util.StringJoiner;

@KeySpace("DocBeans")
public class DocBean implements Serializable {

    private static final long serialVersionUID = -8048620851479991292L;

    @Id
    private Integer id;

    private String firstCode;

    private String secondCode;

    private String content;

    private Integer type;

    private ErrorCode errorCode;

    private Date createTime;

    public DocBean(Integer id, String firstCode, String secondCode, String content, Integer type, ErrorCode errorCode, Date createTime){
        this.id=id;
        this.firstCode=firstCode;
        this.secondCode =secondCode;
        this.content=content;
        this.type=type;
        this.errorCode=errorCode;
        this.createTime=createTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstCode() {
        return firstCode;
    }

    public void setFirstCode(String firstCode) {
        this.firstCode = firstCode;
    }

    public String getSecondCode() {
        return secondCode;
    }

    public void setSecondCode(String secondCode) {
        this.secondCode = secondCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DocBean.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("firstCode='" + firstCode + "'")
                .add("secondCode='" + secondCode + "'")
                .add("content='" + content + "'")
                .add("type=" + type)
                .add("errorCode=" + errorCode)
                .add("createTime=" + createTime)
                .toString();
    }
}
