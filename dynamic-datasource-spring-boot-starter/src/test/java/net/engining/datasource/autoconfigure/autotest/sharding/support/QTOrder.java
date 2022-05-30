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
public class QTOrder extends EntityPathBase<TOrder> {

    private static final long serialVersionUID = -799052121L;

    public static final QTOrder tOrder = new QTOrder("tOrder");

    public final StringPath status = createString("status");

    public final NumberPath<Long> orderId = createNumber("orderId", Long.class);

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public QTOrder(String variable) {
        super(TOrder.class, forVariable(variable));
    }

    public QTOrder(Path<? extends TOrder> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTOrder(PathMetadata metadata) {
        super(TOrder.class, metadata);
    }

}

