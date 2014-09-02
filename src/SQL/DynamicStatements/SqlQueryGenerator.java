/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SQL.DynamicStatements;

import Request.AppRequest.Column;
import Statement.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author T7639192
 */
public class SqlQueryGenerator {

	private static <T> String stringCommaSeperated(Iterable<T> list){
		return stringCommaSeperated(list, false);
	}
	
	private static <T> String stringCommaSeperated(Iterable<T> list, boolean apostrophe) {
		Iterator<T> iterator = list.iterator();
		String res = "";
		while (iterator.hasNext()) {
			if (apostrophe) {
				res += "'" + iterator.next().toString() + "'";
			} else {
				res += iterator.next().toString();
			}
			res += ", ";
		}
		if (res.length() > 0) {
			res = res.substring(0, res.length() - 2);
		}
		return res;
	}

	private static <T> String stringCommaSeperated(Map<T, T> map) {
		Iterator<T> iterator = map.keySet().iterator();
		String res = "";
		while (iterator.hasNext()) {
			T key = iterator.next();
			res += key.toString() + " = '" + map.get(key) + "'";
			res += ", ";
		}
		if (res.length() > 0) {
			res = res.substring(0, res.length() - 2);
		}
		return res;
	}

	public enum ORDER_ORIENTATION {

		ASC, DESC
	}

	public static String select(List<String> colnames, String from, Statement where) {
		return select(colnames, from, where, "", null);
	}

	public static String select(List<String> colnames, String from, Statement where, String orderBy, ORDER_ORIENTATION orie) {
		String columns;
		if (colnames == null) {
			columns = "*";
		} else {
			columns = stringCommaSeperated(colnames);
		}
		// System.out.println("SELECT " + columns + " FROM " + from + " WHERE " + where.toString());
		String query = "SELECT " + columns + " FROM " + from + " WHERE " + where.toString();
		if (orderBy.length() > 0) {
			query += " ORDER BY " + orderBy + " " + orie.toString();
		}
		return query;
	}

	public static String delete(String from, Statement where) {
		return "DELETE FROM " + from + " WHERE " + where.toString();
	}

	public static String update(String table, Map<String, String> set, Statement where) {
		return "UPDATE " + table + " SET " + stringCommaSeperated(set) + " WHERE " + where.toString();
	}
	
	public static String insert(String into, Map<String, String> cols_vals) {
		String sql = "INSERT INTO " + into + "(" + stringCommaSeperated(cols_vals.keySet()) + ") VALUES (" + stringCommaSeperated(cols_vals.values(), true) + ");";
		//sql += "\nSELECT LAST_INSERT_ID( ) AS insert_id;";
		return sql;
	}

	public static String create(String tableName, List<Request.AppRequest.Column> cols) {
		String primaries = "";
		for (Column col : cols) {
			if (col.isPrimary()) {
				primaries += col.getColName() + ", ";
			}
		}
		if (primaries.length() > 0) {
			primaries = primaries.substring(0, primaries.length() - 2);
			primaries = ", PRIMARY KEY (" + primaries + ")";
		}
		return "CREATE TABLE " + tableName + "(" + stringCommaSeperated(cols) + primaries + ")";
	}

	public static String drop(String tableName) {
		return "DROP TABLE " + tableName;
	}

	public static String desc(String tableName) {
		return "DESC " + tableName;
	}
}
