package net.engining.elasticsearch.autoconfigure.autotest.support;

import net.engining.pg.support.core.exception.ErrorCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.io.Serializable;
import java.util.Date;
import java.util.StringJoiner;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2020-07-03 16:13
 * @since :
 **/
@Document(indexName = "#{@environment.getProperty('spring.application.name')}-ems-#{@indexDateSuffix}")
@Setting(replicas = 2)
public class DocBean implements Serializable {

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

    @Field(type = FieldType.Nested, name = "agent")
    private Agent agent;

    public DocBean(PkBean id, String firstCode, String secondCode, String content, Integer type, ErrorCode errorCode,
                   Date createTime, Agent agent){
        this.id=id;
        this.firstCode=firstCode;
        this.secondCode =secondCode;
        this.content=content;
        this.type=type;
        this.errorCode=errorCode;
        this.createTime=createTime;
        this.agent = agent;
    }

    public static class Agent implements Serializable {
        @Field(type = FieldType.Keyword)
        private String hostname;

        @Field(type = FieldType.Keyword)
        private String name;

        public Agent(String hostname, String name) {
            this.hostname = hostname;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getHostname() {
            return hostname;
        }

        public void setHostname(String hostname) {
            this.hostname = hostname;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", Agent.class.getSimpleName() + "[", "]")
                    .add("hostname='" + hostname + "'")
                    .add("name='" + name + "'")
                    .toString();
        }
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
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
                .add("agent=" + agent)
                .toString();
    }
}
