package net.engining.datasource.autoconfigure;

import net.engining.pg.support.enums.BaseEnum;

/**
 * 暂时用于扩展覆盖gp的
 *
 * @author Eric Lu
 */
public enum DbType implements BaseEnum<String> {
	
	DB2("DB2","DB2"),
	
	MySQL("MySQL","MySQL"),
	
	Oracle("Oracle","Oracle"),

	H2("H2", "H2"),

	PostgreSQL("PostgreSQL", "PostgreSQL")

	;


	private final String value;

	private final String label;

	DbType(String value, String label) {
		this.value = value;
		this.label = label;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String getLabel() {
		return label;
	}
}
