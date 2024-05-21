package net.engining.datasource.autoconfigure.support;

import com.querydsl.sql.SQLBaseListener;
import com.querydsl.sql.SQLListenerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2023-05-10 13:16
 * @since :
 **/
public class QsqlLoggingListener extends SQLBaseListener {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(QsqlLoggingListener.class);

    @Override
    public void end(SQLListenerContext context) {
        context.getAllSQLBindings().forEach(sqlBindings -> {
            LOGGER.debug("SQL: {}; Params: {}", sqlBindings.getSQL(), sqlBindings.getNullFriendlyBindings());
        });
    }
}
