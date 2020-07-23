package net.engining.datasource.autoconfigure.autotest.cases;

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
public class QPgIdTestEnt2 extends EntityPathBase<PgIdTestEnt2> {

    private static final long serialVersionUID = -799052121L;

    public static final QPgIdTestEnt2 pgIdTest = new QPgIdTestEnt2("pgIdTestEnt2");

    public final StringPath batchNumber = createString("batchNumber");

    public final NumberPath<Long> snowFlakeId = createNumber("snowFlakeId", Long.class);

    public QPgIdTestEnt2(String variable) {
        super(PgIdTestEnt2.class, forVariable(variable));
    }

    public QPgIdTestEnt2(Path<? extends PgIdTestEnt2> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPgIdTestEnt2(PathMetadata metadata) {
        super(PgIdTestEnt2.class, metadata);
    }

}

