package net.engining.datasource.autoconfigure.autotest.jpa.support;

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
public class QPgIdTestEnt1 extends EntityPathBase<PgIdTestEnt1> {

    private static final long serialVersionUID = -799052121L;

    public static final QPgIdTestEnt1 pgIdTest = new QPgIdTestEnt1("pgIdTestEnt1");

    public final StringPath batchNumber = createString("batchNumber");

    public final NumberPath<Long> snowFlakeId = createNumber("snowFlakeId", Long.class);

    public QPgIdTestEnt1(String variable) {
        super(PgIdTestEnt1.class, forVariable(variable));
    }

    public QPgIdTestEnt1(Path<? extends PgIdTestEnt1> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPgIdTestEnt1(PathMetadata metadata) {
        super(PgIdTestEnt1.class, metadata);
    }

}

