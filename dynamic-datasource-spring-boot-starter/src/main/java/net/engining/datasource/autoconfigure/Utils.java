package net.engining.datasource.autoconfigure;

import cn.hutool.db.dialect.DriverNamePool;
import com.google.common.collect.Table;
import net.engining.pg.support.core.exception.ErrorCode;
import net.engining.pg.support.core.exception.ErrorMessageException;
import net.engining.pg.support.db.DbType;

import javax.sql.DataSource;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-08-03 17:24
 * @since :
 **/
class Utils {

    /**
     * populate the two keys table of datasource
     *
     * @param s                 key of datasource
     * @param dataSource        datasource
     * @param driverClassName   DB driver full class name
     * @param dataSourceTable   struct of dataSourceTable
     */
    protected static void populateDataSourceTable(String s, DataSource dataSource, String driverClassName,
                                 Table<String, DbType, DataSource> dataSourceTable)
    {
        switch (driverClassName){
            case DriverNamePool.DRIVER_DB2:
                dataSourceTable.put(s, DbType.DB2, dataSource);
                break;
            case DriverNamePool.DRIVER_H2:
                dataSourceTable.put(s, DbType.H2, dataSource);
                break;
            case DriverNamePool.DRIVER_MYSQL:
            case DriverNamePool.DRIVER_MYSQL_V6:
                dataSourceTable.put(s, DbType.MySQL, dataSource);
                break;
            case DriverNamePool.DRIVER_ORACLE:
            case DriverNamePool.DRIVER_ORACLE_OLD:
                dataSourceTable.put(s, DbType.Oracle, dataSource);
                break;
            case DriverNamePool.DRIVER_CLICK_HOUSE:
                dataSourceTable.put(s, DbType.ClickHouse, dataSource);
                break;
            default:
                throw new ErrorMessageException(
                        ErrorCode.CheckError,
                        String.format("%s，该类型数据库目前不支持", driverClassName)
                );
        }
    }
}
