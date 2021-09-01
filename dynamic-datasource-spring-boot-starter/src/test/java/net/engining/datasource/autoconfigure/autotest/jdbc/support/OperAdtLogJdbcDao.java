package net.engining.datasource.autoconfigure.autotest.jdbc.support;

import com.google.common.collect.Table;
import net.engining.datasource.autoconfigure.support.AbstractSingleTableJdbcDao;
import net.engining.pg.support.db.DbType;
import net.engining.pg.support.utils.DateUtilsExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
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

    public static final RowMapper<OperAdtLog> ROWMAPPER = (rs, rowNum) -> new OperAdtLog(
            rs.getInt("ID")
            , rs.getString("LOGIN_ID")
            , rs.getString("REQUEST_URI")
            , rs.getString("REQUEST_BODY")
            , DateUtilsExt.dateToLocalDateTime(rs.getTimestamp("OPER_TIME"))
            , rs.getInt("JPA_VERSION")
    );

    @Autowired
    public OperAdtLogJdbcDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                             @Qualifier("multipleDataSourceTable") Table<String, DbType, DataSource> multipleDataSourceTable) {
        super(namedParameterJdbcTemplate, multipleDataSourceTable);
    }

    public Integer insertAndReturnKey(OperAdtLog entity) {
        return getInsert().executeAndReturnKey(new MapSqlParameterSource()
                .addValue("ID", entity.getId())
                .addValue("LOGIN_ID", entity.getLoginId())
                .addValue("REQUEST_URI", entity.getRequestUri())
                .addValue("REQUEST_BODY", entity.getRequestBody())
                .addValue("OPER_TIME", entity.getOperTime())
                .addValue("JPA_VERSION", entity.getJpaVersion())
        ).intValue();
    }

    public void insertBatch(List<OperAdtLog> entitys) {
        MapSqlParameterSource[] batch = new MapSqlParameterSource[entitys.size()];
        for (int i = 0; i < entitys.size(); i++) {
            OperAdtLog entity = entitys.get(i);
            batch[i] = new MapSqlParameterSource()
                    .addValue("ID", entity.getId())
                    .addValue("LOGIN_ID", entity.getLoginId())
                    .addValue("REQUEST_URI", entity.getRequestUri())
                    .addValue("REQUEST_BODY", entity.getRequestBody())
                    .addValue("OPER_TIME", entity.getOperTime())
                    .addValue("JPA_VERSION", entity.getJpaVersion())
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
