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
public class QPgIdSdTestEnt extends EntityPathBase<PgIdSdTestEnt> {

    private static final long serialVersionUID = -799052121L;

    public static final QPgIdSdTestEnt pgIdTest = new QPgIdSdTestEnt("pgIdSdTestEnt");

    public final StringPath batchNumber = createString("batchNumber");

    public final NumberPath<Long> snowFlakeId = createNumber("snowFlakeId", Long.class);

    public QPgIdSdTestEnt(String variable) {
        super(PgIdSdTestEnt.class, forVariable(variable));
    }

    public QPgIdSdTestEnt(Path<? extends PgIdSdTestEnt> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPgIdSdTestEnt(PathMetadata metadata) {
        super(PgIdSdTestEnt.class, metadata);
    }

}

