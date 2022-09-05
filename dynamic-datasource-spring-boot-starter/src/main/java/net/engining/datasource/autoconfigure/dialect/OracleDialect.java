package net.engining.datasource.autoconfigure.dialect;

import org.springframework.data.relational.core.dialect.AnsiDialect;

/**
 * Oracle的方言基本符合ANSI标准，因此直接继承{@link AnsiDialect}已能满足大部分情况；
 * 注意：spring-data-relational-2.0.x 才需要此Dialect，升级到spring-data-relational-2.1.x后不再需要；
 *
 * @author : Eric Lu
 * @version :
 * @date : 2022-03-07 13:02
 * @since :
 **/
@Deprecated
public class OracleDialect extends AnsiDialect {

}
