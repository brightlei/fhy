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
package org.springframework.jdbc.core;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;

import com.alibaba.fastjson.JSONObject;

/**
 * 该文件详细功能描述
 * @author 雷志强
 * @version 1.0
 */
public class ColumnMapRowMapper implements RowMapper<Object>{
	
	public ColumnMapRowMapper() {
	
	}
	
	@Override
	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
//		Map mapOfColValues = createColumnMap(columnCount);
		JSONObject json = new JSONObject();
		for (int i = 1; i <= columnCount; i++) {
			String key = getColumnKey(JdbcUtils.lookupColumnName(rsmd, i)).toLowerCase();
			Object obj = getColumnValue(rs, i);
//			mapOfColValues.put(key, obj);
			json.put(key, obj);
		}
		return json;
	}
	
	@SuppressWarnings("rawtypes")
	protected Map createColumnMap(int columnCount) {
		return new LinkedCaseInsensitiveMap(columnCount);
	}

	protected String getColumnKey(String columnName) {
		return columnName;
	}

	protected Object getColumnValue(ResultSet rs, int index)
			throws SQLException {
		return JdbcUtils.getResultSetValue(rs, index);
	}
}
