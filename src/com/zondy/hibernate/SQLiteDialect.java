/**   
 * 特别声明：本技术材料受《中华人民共和国着作权法》、《计算机软件保护条例》
 * 等法律、法规、行政规章以及有关国际条约的保护，武汉中地数码科技有限公
 * 司享有知识产权、保留一切权利并视其为技术秘密。未经本公司书面许可，任何人
 * 不得擅自（包括但不限于：以非法的方式复制、传播、展示、镜像、上载、下载）使
 * 用，不得向第三方泄露、透露、披露。否则，本公司将依法追究侵权者的法律责任。
 * 特此声明！
 * 
   Copyright (c) 2013,武汉中地数码科技有限公司
 */
package com.zondy.hibernate;

import java.sql.Types;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.Hibernate;

/**
 * 模块名称：SQLiteDialect									<br>
 * 功能描述：该文件详细功能描述							<br>
 * 文档作者：雷志强									<br>
 * 创建时间：2013-10-18 下午04:51:02					<br>
 * 初始版本：V1.0	最初版本号							<br>
 * 修改记录：											<br>
 * *************************************************<br>
 * 修改人：雷志强										<br>
 * 修改时间：2013-10-18 下午04:51:02					<br>
 * 修改内容：重载bindLimitParametersInReverseOrder()方法，修改返回值为true，
 * 解决hibernate连接sqlite进行分页时，不管怎么设置页码，查询结果并不会改变的BUG。
 * *************************************************<br>
 */
public class SQLiteDialect extends Dialect {
	public SQLiteDialect() {
		super();
		registerColumnType(Types.BIT, "integer");
		registerColumnType(Types.TINYINT, "tinyint");
		registerColumnType(Types.SMALLINT, "smallint");
		registerColumnType(Types.INTEGER, "integer");
		registerColumnType(Types.BIGINT, "bigint");
		registerColumnType(Types.FLOAT, "float");
		registerColumnType(Types.REAL, "real");
		registerColumnType(Types.DOUBLE, "double");
		registerColumnType(Types.NUMERIC, "numeric");
		registerColumnType(Types.DECIMAL, "decimal");
		registerColumnType(Types.CHAR, "char");
		registerColumnType(Types.VARCHAR, "varchar");
		registerColumnType(Types.LONGVARCHAR, "longvarchar");
		registerColumnType(Types.DATE, "date");
		registerColumnType(Types.TIME, "time");
		registerColumnType(Types.TIMESTAMP, "timestamp");
		registerColumnType(Types.BINARY, "blob");
		registerColumnType(Types.VARBINARY, "blob");
		registerColumnType(Types.LONGVARBINARY, "blob");
		//registerColumnType(Types.NULL, "text");
		registerColumnType(Types.BLOB, "blob");
		registerColumnType(Types.CLOB, "clob");
		registerColumnType(Types.BOOLEAN, "integer");
		registerFunction("concat", new VarArgsSQLFunction(Hibernate.STRING, "",
				"||", ""));
		registerFunction("mod", new SQLFunctionTemplate(Hibernate.INTEGER,
				"?1 % ?2"));
		registerFunction("substr", new StandardSQLFunction("substr",
				Hibernate.STRING));
		registerFunction("substring", new StandardSQLFunction("substr",
				Hibernate.STRING));
		registerHibernateType(Types.NULL, "string");
	}

	public boolean supportsIdentityColumns() {
		return true;
	}

	/*
	 * public boolean supportsInsertSelectIdentity() { return true; // As
	 * specify in NHibernate dialect }
	 */

	public boolean hasDataTypeInIdentityColumn() {
		return false; // As specify in NHibernate dialect
	}

	/*
	 * public String appendIdentitySelectToInsert(String insertString) { return
	 * new StringBuffer(insertString.length()+30). // As specify in NHibernate
	 * dialect append(insertString).
	 * append("; ").append(getIdentitySelectString()). toString(); }
	 */

	public String getIdentityColumnString() {
		// return "integer primary key autoincrement";
		return "integer";
	}

	public String getIdentitySelectString() {
		return "select last_insert_rowid()";
	}

	public boolean supportsLimit() {
		return true;
	}
	
	public boolean bindLimitParametersInReverseOrder() {
		return true;
	}

	public String getLimitString(String query, boolean hasOffset) {
		return new StringBuffer(query.length() + 20).append(query).append(
				hasOffset ? " limit ? offset ?" : " limit ?").toString();
	}

	public boolean supportsTemporaryTables() {
		return true;
	}

	public String getCreateTemporaryTableString() {
		return "create temporary table if not exists";
	}

	public boolean dropTemporaryTableAfterUse() {
		return false;
	}

	public boolean supportsCurrentTimestampSelection() {
		return true;
	}

	public boolean isCurrentTimestampSelectStringCallable() {
		return false;
	}

	public String getCurrentTimestampSelectString() {
		return "select current_timestamp";
	}

	public boolean supportsUnionAll() {
		return true;
	}

	public boolean hasAlterTable() {
		return false; // As specify in NHibernate dialect
	}

	public boolean dropConstraints() {
		return false;
	}

	public String getAddColumnString() {
		return "add column";
	}

	public String getForUpdateString() {
		return "";
	}

	public boolean supportsOuterJoinForUpdate() {
		return false;
	}

	public String getDropForeignKeyString() {
		throw new UnsupportedOperationException(
				"No drop foreign key syntax supported by SQLiteDialect");
	}

	public String getAddForeignKeyConstraintString(String constraintName,
			String[] foreignKey, String referencedTable, String[] primaryKey,
			boolean referencesPrimaryKey) {
		throw new UnsupportedOperationException(
				"No add foreign key syntax supported by SQLiteDialect");
	}

	public String getAddPrimaryKeyConstraintString(String constraintName) {
		throw new UnsupportedOperationException(
				"No add primary key syntax supported by SQLiteDialect");
	}

	public boolean supportsIfExistsBeforeTableName() {
		return true;
	}

	public boolean supportsCascadeDelete() {
		return false;
	}
}