package net.engining.datasource.autoconfigure.autotest.sharding.support;

import com.google.common.base.MoreObjects;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * t_order
 *
 * @author pg-maven-plugin
 */
@Entity
@Table(name = "t_order_item")
@DynamicInsert(true)
@DynamicUpdate(true)
public class TOrderItem implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "GEN_order_item_id")
    @GenericGenerator(name = "GEN_order_item_id", strategy = "net.engining.pg.support.db.id.generator.SnowflakeSequenceIdGenerator")
    @Column(name = "order_item_id", nullable = false, length = 64)
    private Long orderItemId;

    @Column(name = "order_id", nullable = false, length = 64)
    private Long orderId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "status", nullable = true, length = 500)
    private String status;

    public static final String _orderItemId = "orderItemId";

    public static final String _orderId = "orderId";

    public static final String _userId = "userId";

    public static final String _status = "status";

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .addValue(this.orderItemId)
                .addValue(this.orderId)
                .addValue(this.userId)
                .addValue(this.status)
                .toString();
    }

}