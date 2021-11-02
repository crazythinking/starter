package net.engining.elasticsearch.autoconfigure.autotest.support;

import net.engining.pg.support.core.exception.ErrorCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2020-07-03 16:13
 * @since :
 **/
@Document(indexName = "#{@environment.getProperty('spring.application.name')}-ems")
public class DocBean {

    @Id
    private PkBean id;

    @Field(type = FieldType.Keyword)
    private String firstCode;

    @Field(type = FieldType.Keyword)
    private String secondCode;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Integer)
    private Integer type;

    @Field(type = FieldType.Auto)
    private ErrorCode errorCode;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Date createTime;

    public DocBean(PkBean id, String firstCode, String secondCode, String content, Integer type, ErrorCode errorCode, Date createTime){
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

    public PkBean getId() {
        return id;
    }

    public void setId(PkBean id) {
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
}
