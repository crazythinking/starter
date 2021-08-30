package net.engining.datasource.autoconfigure.autotest.jdbc.support;

import cn.hutool.core.util.HashUtil;
import com.google.common.base.Joiner;
import net.engining.gm.entity.dto.OperAdtLogDto;
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
            operAdtLogExtDto.setId(rs.getInt("ID"));
            operAdtLogExtDto.setLoginId(rs.getString("LOGIN_ID"));
            operAdtLogExtDto.setRequestUri(rs.getString("REQUEST_URI"));
            operAdtLogExtDto.setOperTime(rs.getDate("OPER_TIME"));
            operAdtLogExtDto.setRequestBody(rs.getString("REQUEST_BODY"));
            operAdtLogExtDto.setHashedRequestBody(
                    String.valueOf(
                            HashUtil.tianlHash(
                                    Joiner.on("|").join(
                                            rs.getString("REQUEST_URI"),
                                            rs.getString("REQUEST_BODY"))
                            )
                    )
            );
            return operAdtLogExtDto;
        }
    }
}
