package net.engining.datasource.autoconfigure.autotest.cases;

import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

/**
 * @author Eric Lu
 * @date 2020-07-22 14:55
 **/
public interface QPgIdTestEntInter {

    StringPath batchNumber();
    NumberPath<Long> snowFlakeId();
}
