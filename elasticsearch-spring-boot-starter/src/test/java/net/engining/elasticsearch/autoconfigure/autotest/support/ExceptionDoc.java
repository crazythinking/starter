package net.engining.elasticsearch.autoconfigure.autotest.support;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;
import java.util.StringJoiner;

/**
 * @author : Eric Lu
 * @version :
 * @since : 2023-12-05 10:31
 **/
@Document(indexName = "logstash-exception-error-#{@indexDateSuffix}", createIndex = false)
public class ExceptionDoc implements Serializable {
    @Field(type = FieldType.Date, name = "@timestamp")
    private Date timestamp;

    @Field(type = FieldType.Keyword, name = "Exception_name")
    private String exception;

    @Field(type = FieldType.Text, name = "agent.hostname")
    private String hostname;

    @Field(type = FieldType.Text, name = "project_name")
    private String projectName;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ExceptionDoc.class.getSimpleName() + "[", "]")
                .add("timestamp=" + timestamp)
                .add("exception='" + exception + "'")
                .add("hostname='" + hostname + "'")
                .add("projectName='" + projectName + "'")
                .toString();
    }
}
