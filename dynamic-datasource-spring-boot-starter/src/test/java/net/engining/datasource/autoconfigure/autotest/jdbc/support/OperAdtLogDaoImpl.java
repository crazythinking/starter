package net.engining.datasource.autoconfigure.autotest.jdbc.support;

import com.google.common.collect.Lists;
import net.engining.datasource.autoconfigure.autotest.support.OperAdtLogDao;
import net.engining.gm.aop.SpecifiedDataSource;
import net.engining.gm.entity.dto.OperAdtLogDto;
import net.engining.pg.support.utils.DateUtilsExt;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Eric Lu
 * @date 2020-12-30 14:54
 **/
@Repository
public class OperAdtLogDaoImpl extends SingleTableBaseDao<Integer, JdbcOperAdtLog> implements OperAdtLogDao {

    @Autowired
    protected Dao baseDao;

    public static final RowMapper<JdbcOperAdtLog> ROWMAPPER = new RowMapper<JdbcOperAdtLog>() {

        @Override
        public JdbcOperAdtLog mapRow(ResultSet rs, int rowNum) throws SQLException {

            return new JdbcOperAdtLog(
                    rs.getInt("ID")
                    , rs.getString("LOGIN_ID")
                    , rs.getString("REQUEST_URI")
                    , rs.getString("REQUEST_BODY")
                    , DateUtilsExt.dateToLocalDateTime(rs.getTimestamp("OPER_TIME"))
                    , rs.getInt("JPA_VERSION")
            );
        }
    };

    @Override
    @SpecifiedDataSource("one")
    @Transactional(rollbackFor = Exception.class)
    public int insert(JdbcOperAdtLog entity) {
        return super.insert(new MapSqlParameterSource()
                .addValue("ID", entity.getId())
                .addValue("LOGIN_ID", entity.getLoginId())
                .addValue("REQUEST_URI", entity.getRequestUri())
                .addValue("REQUEST_BODY", entity.getRequestBody())
                .addValue("OPER_TIME", entity.getOperTime())
                .addValue("JPA_VERSION", entity.getJpaVersion())
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer insertAndReturnKey(JdbcOperAdtLog entity) {
        return super.insertAndReturnKey(new MapSqlParameterSource()
                .addValue("ID", entity.getId())
                .addValue("LOGIN_ID", entity.getLoginId())
                .addValue("REQUEST_URI", entity.getRequestUri())
                .addValue("REQUEST_BODY", entity.getRequestBody())
                .addValue("OPER_TIME", entity.getOperTime())
                .addValue("JPA_VERSION", entity.getJpaVersion())
        ).intValue();
    }

    @Override
    @SpecifiedDataSource("one")
    @Transactional(rollbackFor = Exception.class)
    public int[] insert(List<JdbcOperAdtLog> entitys) {
        MapSqlParameterSource[] batch = new MapSqlParameterSource[entitys.size()];
        for (int i = 0; i < entitys.size(); i++) {
            JdbcOperAdtLog entity = entitys.get(i);
            batch[i] = new MapSqlParameterSource()
                    .addValue("ID", entity.getId())
                    .addValue("LOGIN_ID", entity.getLoginId())
                    .addValue("REQUEST_URI", entity.getRequestUri())
                    .addValue("REQUEST_BODY", entity.getRequestBody())
                    .addValue("OPER_TIME", entity.getOperTime())
                    .addValue("JPA_VERSION", entity.getJpaVersion())
            ;
        }
        return super.insertBatch(batch);
    }

    @Override
    public int updateByPrimaryKey(JdbcOperAdtLog entity) {
        return 0;
    }

    @Override
    public int updateByPrimaryKeySelective(JdbcOperAdtLog entity) {
        return 0;
    }

    @Override
    @SpecifiedDataSource("one")
    public JdbcOperAdtLog selectByPrimaryKey(Integer key) {
        return baseDao.queryForObject(
                "SELECT ID, LOGIN_ID, REQUEST_URI, REQUEST_BODY, OPER_TIME, JPA_VERSION FROM OPER_ADT_LOG WHERE ID = :ID",
                ROWMAPPER,
                new MapSqlParameterSource().addValue("ID", key)
        );
    }

    @Override
    public int delete(Integer key) {
        return 0;
    }

    @Override
    public int[] delete(List<Integer> keys) {
        return new int[0];
    }

    @Override
    protected String getTableName() {
        return "OPER_ADT_LOG";
    }

    @Override
    protected String[] getKeyColumns() {
        return new String[]{};
        //return new String[]{"ID"};
    }

    @Override
    public long save(List<OperAdtLogDto> operAdtLogs) {
        List<JdbcOperAdtLog> entitys = Lists.newArrayList();
        operAdtLogs.forEach(operAdtLogDto -> {
            JdbcOperAdtLog entity = new JdbcOperAdtLog(
                    operAdtLogDto.getId(),
                    operAdtLogDto.getLoginId(),
                    operAdtLogDto.getRequestUri(),
                    operAdtLogDto.getRequestBody(),
                    DateUtilsExt.dateToLocalDateTime(operAdtLogDto.getOperTime()),
                    0
            );
            entitys.add(entity);
        });
        insert(entitys);
        return 0;
    }

    @Override
    public OperAdtLogDto findByPrimeryKey(Integer id) {
        JdbcOperAdtLog jdbcOperAdtLog = selectByPrimaryKey(id);
        OperAdtLogDto operAdtLogDto = new OperAdtLogDto();
        BeanUtils.copyProperties(jdbcOperAdtLog, operAdtLogDto);
        return operAdtLogDto;
    }
}
