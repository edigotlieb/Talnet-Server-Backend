/**
 * FILE : SqlQueryGenerator.java AUTHORS : Idan Berkovits
 */
package SQL.DynamicStatements;

import Request.Credentials;
import RequestArgumentAssignment.RequestArgumentStructureAssignment;
import SQL.PreparedStatements.StatementPreparerArgument;
import SQL.SqlExecutor;
import Statement.AndStatement;
import Statement.BaseStatement;
import Statement.Statement;
import Utilities.Sql;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * this abstract class generates all dynamic sql queries
 *
 * @author idanb55
 */
public abstract class SqlQueryGenerator {

	private final static Map<String, SqlQueryGenerator> queryGenerators;
	public static List<String> selectUserColumns;

	/**
	 * loads the columns of USERS table into a list to use in user select
	 * request
	 *
	 * @param sqlExc the sql query executor
	 * @throws SQLException thrown in case of an sql execution exception
	 */
	public static void loadUserColumns(SqlExecutor sqlExc) throws SQLException {
		SqlQueryGenerator.selectUserColumns = Sql.getColNames(sqlExc, "USERS");
		selectUserColumns.remove("PASSWORD");
		selectUserColumns.add("CONCAT_WS(' ',USERS.FIRST_NAME,USERS.LAST_NAME) AS NAME");
	}

	static {
		queryGenerators = new HashMap<>();
		queryGenerators.put("userSelect", new SqlQueryGenerator() {
			@Override
			public String generateQuery(StatementPreparerArgument sp) {
				String cols = "";
				Iterator<String> columns = SqlQueryGenerator.selectUserColumns.iterator();
				while (columns.hasNext()) {
					cols += columns.next();
					if (columns.hasNext()) {
						cols += ", ";
					}
				}
				Statement whereNoAnon = new AndStatement(sp.getArgumentStatement(1), new BaseStatement("USERNAME", Credentials.anonymous, "!="));
				return "SELECT " + cols + " FROM USERS HAVING " + whereNoAnon + " ORDER BY NAME ASC";
			}
		});
		queryGenerators.put("select", new SqlQueryGenerator() {
			@Override
			public String generateQuery(StatementPreparerArgument sp) {
				String query = "SELECT * FROM ";
				String appName = sp.getArgumentValue(1);
				Iterator<RequestArgumentStructureAssignment> tables = sp.getArgumentList(2).iterator();
				query += appName + "_" + tables.next().getArgument("tableName");
				for (; tables.hasNext(); query += " NATURAL JOIN " + appName + "_" + tables.next().getArgument("tableName")) {
				}
				query += " WHERE " + sp.getArgumentStatement(3);
				if (sp.getArgumentValue(3).length() > 0) {
					query += " ORDER BY " + sp.getArgumentValue(4) + " " + sp.getArgumentValue(5);
				}
				return query;
			}
		});
		queryGenerators.put("count", new SqlQueryGenerator() {
			@Override
			public String generateQuery(StatementPreparerArgument sp) {
				return "SELECT count(*) AS resultLength FROM " + sp.getArgumentValue(1) + " WHERE " + sp.getArgumentStatement(2);
			}
		});
		queryGenerators.put("insert", new SqlQueryGenerator() {
			@Override
			public String generateQuery(StatementPreparerArgument sp) {
				String sql = "INSERT INTO " + sp.getArgumentValue(1);
				String cols = "";
				String vals = "";
				Map<String, String> columnMap = sp.getArgumentMap(2);
				Iterator<String> columns = columnMap.keySet().iterator();
				while (columns.hasNext()) {
					String column = columns.next();
					cols += column;
					if (columnMap.get(column) != null) {
						vals += "'" + Utilities.Sql.sanitizeSqlCharacterEscaping(columnMap.get(column)) + "'";
					} else {
						vals += "NULL";
					}
					if (columns.hasNext()) {
						cols += ", ";
						vals += ", ";
					}
				}
				sql += " (" + cols + ") VALUES (" + vals + ")";
				return sql;
			}
		});
		queryGenerators.put("delete", new SqlQueryGenerator() {
			@Override
			public String generateQuery(StatementPreparerArgument sp) {
				return "DELETE FROM " + sp.getArgumentValue(1) + " WHERE " + sp.getArgumentStatement(2);
			}
		});
		queryGenerators.put("update", new SqlQueryGenerator() {
			@Override
			public String generateQuery(StatementPreparerArgument sp) {
				String sql = "UPDATE " + sp.getArgumentValue(1) + " SET ";
				String data = "";
				Map<String, String> columnMap = sp.getArgumentMap(2);
				Iterator<String> columns = columnMap.keySet().iterator();
				while (columns.hasNext()) {
					String column = columns.next();
					if (columnMap.get(column) != null) {
						data += column + " = '" + Utilities.Sql.sanitizeSqlCharacterEscaping(columnMap.get(column)) + "'";
					} else {
						data += column + " = NULL";
					}
					if (columns.hasNext()) {
						data += ", ";
					}
				}
				sql += data + " WHERE " + sp.getArgumentStatement(3);
				return sql;
			}
		});
		queryGenerators.put("createTable", new SqlQueryGenerator() {
			@Override
			public String generateQuery(StatementPreparerArgument sp) {
				String cols = "";
				String primaries = "";
				Iterator<RequestArgumentStructureAssignment> columns = sp.getArgumentList(2).iterator();
				while (columns.hasNext()) {
					RequestArgumentStructureAssignment column = columns.next();
					cols += columnToString(column);
					if (((String) column.getArgument("isPrimary").getValue()).toLowerCase().equals("true")) {
						primaries += (String) column.getArgument("colName").getValue() + ", ";
					}
					if (columns.hasNext()) {
						cols += ", ";
					}
				}
				primaries = primaries.substring(0, primaries.length() - 2);
				return "CREATE TABLE " + sp.getArgumentValue(1) + " (" + cols + ", PRIMARY KEY (" + primaries + ") )";
			}

			private String columnToString(RequestArgumentStructureAssignment column) {
				String col = column.getArgument("colName").getValue() + " " + column.getArgument("type").getValue();
				switch ((String) column.getArgument("type").getValue()) {
					case "DATE":
					case "TIMESTAMP":
					case "FLOAT":
						break;
					case "VARCHAR":
						if (Integer.parseInt((String) column.getArgument("size").getValue()) <= 0) {
							col += "(50)";
						} else {
							col += "(" + (String) column.getArgument("size").getValue() + ")";
						}
						break;
					case "INTEGER":
						if (Integer.parseInt((String) column.getArgument("size").getValue()) <= 0) {
							col += "(11)";
						} else {
							col += "(" + (String) column.getArgument("size").getValue() + ")";
						}
						break;
				}
				if (((String) column.getArgument("autoInc").getValue()).toLowerCase().equals("true")) {
					col += " AUTO_INCREMENT";
				}
				return col;
			}
		});
		queryGenerators.put("descTable", new SqlQueryGenerator() {
			@Override
			public String generateQuery(StatementPreparerArgument sp) {
				return "DESC " + sp.getArgumentValue(1);
			}
		});
		queryGenerators.put("dropTable", new SqlQueryGenerator() {
			@Override
			public String generateQuery(StatementPreparerArgument sp) {
				return "DROP TABLE " + sp.getArgumentValue(1);
			}
		});
	}

	public abstract String generateQuery(StatementPreparerArgument sp);

	public static SqlQueryGenerator getQueryGenerator(String query) {
		return queryGenerators.get(query);
	}
}
