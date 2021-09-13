package net.engining.datasource.autoconfigure.autotest.jdbc.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.util.Date;

/**
 * 批量处理数据主键信息表
 * @author pg-maven-plugin
 */
@SuppressWarnings("AliControlFlowStatementWithoutBraces")
@Table("PG_KEY_CONTEXT")
public class PgKeyContext implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private PgKeyContextKey pgKeyContextKey;

    @Column("SETUP_DATE")
    private Date setupDate;

    @Column("JPA_VERSION")
    @Version
    @JsonIgnore
    private Integer jpaVersion;

    public static final String P_ContextId = "contextId";

    public static final String P_CONTEXT_ID = "CONTEXT_ID";

    public static final String P_KeyList = "keyList";

    public static final String P_KEY_LIST = "KEY_LIST";

    public static final String P_SetupDate = "setupDate";

    public static final String P_SETUP_DATE = "SETUP_DATE";

    public static final String P_JpaVersion = "jpaVersion";

    public static final String P_JPA_VERSION = "JPA_VERSION";

    public PgKeyContextKey getPgKeyContextKey() {
        return pgKeyContextKey;
    }

    public void setPgKeyContextKey(PgKeyContextKey pgKeyContextKey) {
        this.pgKeyContextKey = pgKeyContextKey;
    }

    /**
     * <p>数据产生时间</p>
     */
    public Date getSetupDate() {
        return setupDate;
    }

    /**
     * <p>数据产生时间</p>
     */
    public void setSetupDate(Date setupDate) {
        this.setupDate = setupDate;
    }

    /**
     * <p>乐观锁版本号</p>
     */
    public Integer getJpaVersion() {
        return jpaVersion;
    }

    /**
     * <p>乐观锁版本号</p>
     */
    public void setJpaVersion(Integer jpaVersion) {
        this.jpaVersion = jpaVersion;
    }

    public void fillDefaultValues() {
        if (jpaVersion == null) jpaVersion = 0;
    }

}