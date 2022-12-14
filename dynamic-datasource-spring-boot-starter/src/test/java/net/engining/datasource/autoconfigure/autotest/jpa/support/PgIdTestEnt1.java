package net.engining.datasource.autoconfigure.autotest.jpa.support;

import com.google.common.base.MoreObjects;
import net.engining.pg.support.meta.PropertyInfo;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;
import java.io.Serializable;

/**
 * PG_ID_TEST
 * @author pg-maven-plugin
 */
@Entity
@Table(name="PG_ID_TEST_Ent1")
@DynamicInsert(true)
@DynamicUpdate(true)
@FilterDef(
        name = "org",
        parameters = {
                @ParamDef(name = "org", type = "string")
        }
)
@Filter(name = "org", condition = "ORG = :org")
public class PgIdTestEnt1 implements Serializable {
    private static final long serialVersionUID = 1L;

    @PropertyInfo(name="SNOW_FLAKE_ID", length=64)
    @Id
    @GeneratedValue(generator="GEN_PG_ID_TEST")
    @GenericGenerator(name="GEN_PG_ID_TEST", strategy="net.engining.pg.support.db.id.generator.SnowflakeSequenceIdGenerator")
    @Column(name="SNOW_FLAKE_ID", nullable=false, length=64)
    private Long snowFlakeId;

    @PropertyInfo(name="\u6279\u6267\u884C\u5E8F\u53F7", length=50)
    @Column(name="BATCH_NUMBER", nullable=true, length=50)
    private String batchNumber;

    @Column(name="ORG", nullable=true, length=10)
    private String org;

    public static final String _snowFlakeId = "snowFlakeId";

    public static final String _batchNumber = "batchNumber";

    public static final String _org = "org";

    /**
     * <p>SNOW_FLAKE_ID</p>
     * <p>###net.engining.pg.support.db.id.generator.SnowflakeSequenceIdGenerator###</p>
     */
    public Long getSnowFlakeId() {
        return snowFlakeId;
    }

    /**
     * <p>SNOW_FLAKE_ID</p>
     * <p>###net.engining.pg.support.db.id.generator.SnowflakeSequenceIdGenerator###</p>
     */
    public void setSnowFlakeId(Long snowFlakeId) {
        this.snowFlakeId = snowFlakeId;
    }

    /**
     * <p>???????????????</p>
     */
    public String getBatchNumber() {
        return batchNumber;
    }

    /**
     * <p>???????????????</p>
     */
    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
        	.addValue(this.snowFlakeId)
        	.addValue(this.batchNumber)
            .addValue(this.org)
        	.toString();
    }
}