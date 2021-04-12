package net.engining.datasource.autoconfigure.autotest.jdbc.support;

import org.springframework.data.convert.ClassGeneratingEntityInstantiator;

import java.time.LocalDateTime;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2020-12-30 15:55
 * @since :
 **/
public class OperAdtLogInstantiator implements ClassGeneratingEntityInstantiator.ObjectInstantiator {
    @Override
    public Object newInstance(Object... args) {
        return new OperAdtLog(
                (Integer) args[0],
                (String) args[1],
                (String) args[2],
                (String) args[3],
                (LocalDateTime) args[4],
                (Integer) args[5]
        );
    }
}
