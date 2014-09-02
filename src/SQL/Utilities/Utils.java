/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SQL.Utilities;

import Request.AppRequest.Permission;
import SQL.DynamicStatements.SqlQueryGenerator;
import SQL.PreparedStatements.StatementPreparer;
import SQL.SqlExecutor;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author t7639192
 */
public class Utils {

	public static String sanitizeAlphaNumeric(String org) {
		return org.replaceAll("[^\\w]", "");
	}

	public static String sanitizeSqlCharacterEscaping(String org) {
		//String hebrewChars = "אבגדהוזחטיכךלמםנןסעפףצץקרשת";
		//String special = "\\:\\ \\.\\*\\+\\-\\(\\)\\[\\]\\{\\}\\\\" + hebrewChars;
		//return org.replaceAll("[^\\w" + special + "]", "");
		return org.replaceAll("'", "''");
	}

	public static boolean isAlphaNumeric(String org) {
		return org.matches("[\\w]+");
	}

	public static String toString(Date date) {

		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                
		return sdf.format(date);
	}
	
	public static String toString(Timestamp tstamp) {

		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		return sdf.format(tstamp);
	}

	public static List<String> getColNames(SqlExecutor sqlExc, String table) throws SQLException {
		List<String> cols = new ArrayList<>();
		ResultSet rset = sqlExc.executeDynamicStatementQry(SqlQueryGenerator.desc(table));
		while (rset.next()) {
			cols.add(rset.getString(1));
		}
		return cols;
	}
	
	public static List<Permission.PERMISSION_TYPE> getGroupPermissionsForTable(SqlExecutor sqlExc, String table, String group) throws SQLException {
		List<Permission.PERMISSION_TYPE> pers = new ArrayList<>();
		final String group_name = group;
		final String table_name = table;
		ResultSet rset = sqlExc.executePreparedStatement("getGroupTablePermission", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, group_name);
				ps.setString(2, table_name);
			}
		});
		while (rset.next()) {
			pers.add(Permission.PERMISSION_TYPE.valueOf(rset.getString("PERMISSION_TYPE")));
		}
		return pers;
	}
	
	public static int countOccurances(String str, String count){
		return str.length() - str.replace(count, "").length();
	}
}
