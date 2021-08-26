package net.engining.datasource.autoconfigure.autotest.support;

import java.util.Date;

/**
 * @author Eric Lu
 * @date 2021-08-24 11:27
 **/
public interface OperAdtLogProjection {
    Integer getId();

    String getLoginId();

    String getRequestUri();

    Date getOperTime();
}
