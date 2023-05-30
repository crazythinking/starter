package net.engining.mongo.autoconfigure.autotest.support;

import net.engining.pg.support.utils.DateUtilsExt;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.StringJoiner;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2023-05-19 15:52
 * @since :
 **/
@Document(collection = "kv_lob")
public class KvLob {
    @Id
    private String id;

    private String jsonValue;

    @CreatedDate
    private Date createTime;

    @LastModifiedDate
    private Date updateTime;

    public KvLob() {
        this.createTime = new Date();
        this.updateTime = new Date();
    }

    public KvLob(String id, String jsonValue) {
        this.id = id;
        this.jsonValue = jsonValue;
        this.createTime = DateUtilsExt.localDateTimeToDate(LocalDateTime.now());
        this.updateTime = DateUtilsExt.localDateTimeToDate(LocalDateTime.now());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJsonValue() {
        return jsonValue;
    }

    public void setJsonValue(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", KvLob.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("jsonValue='" + jsonValue + "'")
                .add("createTime=" + createTime)
                .add("updateTime=" + updateTime)
                .toString();
    }
}
