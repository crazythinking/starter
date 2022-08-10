package net.engining.datasource.autoconfigure.autotest.jdbc.support;

import org.springframework.data.relational.core.mapping.Column;

import java.io.Serializable;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-09-09 16:20
 * @since :
 **/
public class PgKeyContextKey implements Serializable {

    @Column("CONTEXT_ID")
    private Integer contextId;

    @Column("CONTEXT_ID2")
    private Integer contextId2;

    public PgKeyContextKey(Integer contextId, Integer contextId2) {
        this.contextId = contextId;
        this.contextId2 = contextId2;
    }

    public Integer getContextId() {
        return contextId;
    }

    public void setContextId(Integer contextId) {
        this.contextId = contextId;
    }

    public Integer getContextId2() {
        return contextId2;
    }

    public void setContextId2(Integer contextId2) {
        this.contextId2 = contextId2;
    }
}
