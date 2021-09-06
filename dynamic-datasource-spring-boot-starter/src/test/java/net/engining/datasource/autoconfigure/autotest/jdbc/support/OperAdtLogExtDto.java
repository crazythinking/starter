package net.engining.datasource.autoconfigure.autotest.jdbc.support;

import cn.hutool.core.util.HashUtil;
import com.google.common.base.Joiner;
import net.engining.gm.entity.dto.OperAdtLogDto;
import net.engining.gm.entity.model.jdbc.OperAdtLog;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-08-26 10:04
 * @since :
 **/
public class OperAdtLogExtDto extends OperAdtLogDto {

    private String hashedRequestBody;

    public String getHashedRequestBody() {
        return hashedRequestBody;
    }

    public void setHashedRequestBody(String hashedRequestBody) {
        this.hashedRequestBody = hashedRequestBody;
    }

    public static class OperAdtLogExtDtoRowMapper implements RowMapper<OperAdtLogExtDto> {

        @Override
        public OperAdtLogExtDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            OperAdtLogExtDto operAdtLogExtDto = new OperAdtLogExtDto();
            operAdtLogExtDto.setId(rs.getInt(OperAdtLog.P_ID));
            operAdtLogExtDto.setLoginId(rs.getString(OperAdtLog.P_LOGIN_ID));
            operAdtLogExtDto.setRequestUri(rs.getString(OperAdtLog.P_REQUEST_URI));
            operAdtLogExtDto.setOperTime(rs.getDate(OperAdtLog.P_OPER_TIME));
            operAdtLogExtDto.setRequestBody(rs.getString(OperAdtLog.P_REQUEST_BODY));
            operAdtLogExtDto.setHashedRequestBody(
                    String.valueOf(
                            HashUtil.tianlHash(
                                    Joiner.on("|").join(
                                            rs.getString(OperAdtLog.P_REQUEST_URI),
                                            rs.getString(OperAdtLog.P_REQUEST_BODY))
                            )
                    )
            );
            return operAdtLogExtDto;
        }
    }
}
