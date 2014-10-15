/**
 * FILE : Sql.java AUTHORS : Idan Berkovits
 */
package Utilities;

import SQL.SqlExecutor;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * defines sql utilities
 */
public class Sql {

	/**
	 * deletes every non alpha numeric characters
	 *
	 * @param org the original String
	 * @return the sanitized String
	 */
	public static String sanitizeAlphaNumeric(String org) {
		return org.replaceAll("[^\\w]", "");
	}

	/**
	 * escapes sql character to assign as a string
	 *
	 * @param org the original String
	 * @return the sanitized String
	 */
	public static String sanitizeSqlCharacterEscaping(String org) {
		return org.replaceAll("'", "''");
	}

	/**
	 * returns whether all character of a string are alpha numeric
	 *
	 * @param org the original String
	 * @return the sanitized String
	 */
	public static boolean isAlphaNumeric(String org) {
		return org.matches("[\\w]+");
	}

	/**
	 * returns a date in sql format
	 *
	 * @param date the date to describe
	 * @return the result string
	 */
	public static String toString(Date date) {
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}

	/**
	 * returns a timestamp in sql format
	 *
	 * @param tstamp the timestamp to describe
	 * @return the result string
	 */
	public static String toString(Timestamp tstamp) {
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(tstamp);
	}

	/**
	 * get a list of all column names of a given table
	 *
	 * @param sqlExc sql executor
	 * @param table the table to get its description
	 * @return the column name list
	 * @throws SQLException thrown in case of sql execution exception
	 */
	public static List<String> getColNames(SqlExecutor sqlExc, String table) throws SQLException {
		List<String> cols = new ArrayList<>();
		ResultSet rset = sqlExc.executeDynamicStatementQry("DESC " + table, null);
		while (rset.next()) {
			cols.add(rset.getString(1));
		}
		return cols;
	}
}
