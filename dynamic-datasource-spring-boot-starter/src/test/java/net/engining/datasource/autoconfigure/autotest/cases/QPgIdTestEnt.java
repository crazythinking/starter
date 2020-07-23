package net.engining.datasource.autoconfigure.autotest.cases;

import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2020-07-22 14:30
 * @since :
 **/
public class QPgIdTestEnt implements QPgIdTestEntInter{
    private int index;

    public QPgIdTestEnt(int index) {
        this.index = index;
    }

    public EntityPathBase getQ(){
        if (index == 1) {
            return QPgIdTestEnt1.pgIdTest;
        }
        else if (index == 2) {
            return QPgIdTestEnt2.pgIdTest;
        }

        return null;
    }

    @Override
    public StringPath batchNumber() {
        if (index == 1) {
            return QPgIdTestEnt1.pgIdTest.batchNumber;
        }
        else if (index == 2) {
            return QPgIdTestEnt2.pgIdTest.batchNumber;
        }
        return null;
    }

    @Override
    public NumberPath<Long> snowFlakeId() {
        if (index == 1) {
            return QPgIdTestEnt1.pgIdTest.snowFlakeId;
        }
        else if (index == 2) {
            return QPgIdTestEnt2.pgIdTest.snowFlakeId;
        }
        return null;
    }

}
