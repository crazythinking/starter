package net.engining.datasource.autoconfigure.autotest.jdbc.support;

import com.google.common.collect.Table;
import net.engining.datasource.autoconfigure.support.Utils;
import net.engining.gm.entity.model.jdbc.OperAdtLog;
import net.engining.pg.support.db.DbType;
import net.engining.pg.support.db.jdbc.AbstractSingleTableJdbcDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

/**
 * 面向单表的, 基于纯粹的Spring-JDBC操作的存储层
 * @author Eric Lu
 * @date 2020-12-30 14:54
 **/
@Component
public class OperAdtLogJdbcDao extends AbstractSingleTableJdbcDao {

    @Autowired
    public OperAdtLogJdbcDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                             @Qualifier(Utils.MULTIPLE_DATA_SOURCE_TABLE) Table<String, DbType, DataSource> multipleDataSourceTable) {
        super(namedParameterJdbcTemplate, multipleDataSourceTable);
    }

    public void insertBatch(List<OperAdtLog> entitys) {
        MapSqlParameterSource[] batch = new MapSqlParameterSource[entitys.size()];
        for (int i = 0; i < entitys.size(); i++) {
            OperAdtLog entity = entitys.get(i);
            batch[i] = new MapSqlParameterSource()
                    .addValue(OperAdtLog.P_ID, entity.getId())
                    .addValue(OperAdtLog.P_LOGIN_ID, entity.getLoginId())
                    .addValue(OperAdtLog.P_REQUEST_URI, entity.getRequestUri())
                    .addValue(OperAdtLog.P_REQUEST_BODY, entity.getRequestBody())
                    .addValue(OperAdtLog.P_OPER_TIME, entity.getOperTime())
                    .addValue(OperAdtLog.P_JPA_VERSION, 0)
            ;
        }
        getInsert().executeBatch(batch);

    }

    @Override
    protected String getTableName() {
        return "OPER_ADT_LOG";
    }

    @Override
    protected String[] getKeyColumns() {
        return new String[]{"ID"};
    }

}
