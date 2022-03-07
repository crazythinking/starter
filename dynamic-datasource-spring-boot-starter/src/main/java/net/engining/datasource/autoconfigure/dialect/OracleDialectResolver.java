package net.engining.datasource.autoconfigure.dialect;


import org.springframework.data.jdbc.repository.config.DialectResolver;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcOperations;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2022-03-07 13:55
 * @since :
 **/
public class OracleDialectResolver implements DialectResolver.JdbcDialectProvider {

    public static final String ORACLE = "oracle";

    @Override
    public Optional<Dialect> getDialect(JdbcOperations operations) {
        return Optional.ofNullable(operations.execute((ConnectionCallback<Dialect>) OracleDialectResolver::getDialect));
    }

    private static Dialect getDialect(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        String name = metaData.getDatabaseProductName().toLowerCase(Locale.ROOT);
        if (name.contains(ORACLE)) {
            return OracleDialect.INSTANCE;
        }
        return null;
    }
}
