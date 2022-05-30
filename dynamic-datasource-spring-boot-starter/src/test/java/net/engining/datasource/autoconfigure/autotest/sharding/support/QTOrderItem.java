package net.engining.datasource.autoconfigure.autotest.sharding.support;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

import javax.annotation.Generated;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QPgIdTest is a Querydsl query type for PgIdTest
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QTOrderItem extends EntityPathBase<TOrderItem> {

    private static final long serialVersionUID = -799052121L;

    public static final QTOrderItem tOrderItem = new QTOrderItem("tOrderItem");

    public final StringPath status = createString("status");

    public final NumberPath<Long> orderItemId = createNumber("orderItemId", Long.class);

    public final NumberPath<Long> orderId = createNumber("orderId", Long.class);

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public QTOrderItem(String variable) {
        super(TOrderItem.class, forVariable(variable));
    }

    public QTOrderItem(Path<? extends TOrderItem> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTOrderItem(PathMetadata metadata) {
        super(TOrderItem.class, metadata);
    }

}

