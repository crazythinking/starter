package net.engining.datasource.autoconfigure.autotest.jdbc.support;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2020-12-30 12:33
 * @since :
 **/
@Table("OPER_ADT_LOG")
public class OperAdtLog {

    @Id
    @Column("ID")
    private Integer id;

    @Column("LOGIN_ID")
    private String loginId;

    @Column("REQUEST_URI")
    private String requestUri;

    @Column("REQUEST_BODY")
    private String requestBody;

    @Column("OPER_TIME")
    private LocalDateTime operTime;

    @Column("JPA_VERSION")
    private Integer jpaVersion;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public LocalDateTime getOperTime() {
        return operTime;
    }

    public void setOperTime(LocalDateTime operTime) {
        this.operTime = operTime;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public Integer getJpaVersion() {
        return jpaVersion;
    }

    public void setJpaVersion(Integer jpaVersion) {
        this.jpaVersion = jpaVersion;
    }

    public OperAdtLog(Integer id, String loginId, String requestUri, String requestBody, LocalDateTime operTime, Integer jpaVersion) {
        this.id = id;
        this.loginId = loginId;
        this.requestUri = requestUri;
        this.requestBody = requestBody;
        this.operTime = operTime;
        this.jpaVersion = jpaVersion;
    }

    @Override
    public String toString() {
        return "OperAdtLog{" +
                "id=" + id +
                ", loginId='" + loginId + '\'' +
                ", requestUri='" + requestUri + '\'' +
                ", requestBody='" + requestBody + '\'' +
                ", operTime=" + operTime +
                ", jpaVersion=" + jpaVersion +
                '}';
    }
}
