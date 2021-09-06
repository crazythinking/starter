package net.engining.datasource.autoconfigure.autotest.jpa.support;

import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 该拦截器的用处不大，粒度太粗；会截取所有SQL，且由于传入的是SQL-String，需要自行实现SQL Parse；
 * @author : Eric Lu
 * @version :
 * @date : 2021-09-03 16:42
 * @since :
 **/
public class OrgInterceptor implements StatementInspector {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(OrgInterceptor.class);

    @Override
    public String inspect(String sql) {
        LOGGER.debug(sql);
        return sql;
    }
}
