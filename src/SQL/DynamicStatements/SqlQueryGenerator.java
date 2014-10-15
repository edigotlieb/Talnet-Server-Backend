/**
 * FILE : SqlQueryGenerator.java AUTHORS : Idan Berkovits
 */
package SQL.DynamicStatements;

import Request.Credentials;
import Request.Preformance.DynamicSqlExecutePerformance;
import RequestArgumentAssignment.RequestArgumentStructureAssignment;
import SQL.PreparedStatements.StatementPreparerArgument;
import Statement.AndStatement;
import Statement.RelStatement;
import Statement.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * this abstract class generates all dynamic sql queries
 *
 * @author idanb55
 */
public abstract class SqlQueryGenerator {

	private final static Map<String, SqlQueryGenerator> queryGenerators;

	static {
		queryGenerators = new HashMap<>();
		queryGenerators.put("userSelect", new SqlQueryGenerator() {
			@Override
			public String generateQuery(StatementPreparerArgument sp) {
				String cols = "";
				Iterator<String> columns = DynamicSqlExecutePerformance.selectUserColumns.iterator();
				while (columns.hasNext()) {
					cols += columns.next();
					if (columns.hasNext()) {
						cols += ", ";
					}
				}
				Statement whereNoAnon = new AndStatement(sp.getArgumentStatement(1), new RelStatement("USERNAME", Credentials.anonymous, "!="));
				return "SELECT " + cols + " FROM USERS WHERE " + whereNoAnon + " ORDER BY NAME ASC";
			}
		});
		queryGenerators.put("select", new SqlQueryGenerator() {
			@Override
			public String generateQuery(StatementPreparerArgument sp) {
				if (sp.getArgumentValue(3).length() > 0) {
					return "SELECT * FROM " + sp.getArgumentValue(1) + " WHERE " + sp.getArgumentStatement(2) + " ORDER BY " + sp.getArgumentValue(3) + " " + sp.getArgumentValue(4);
				}
				return "SELECT * FROM " + sp.getArgumentValue(1) + " WHERE " + sp.getArgumentStatement(2);
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
					vals += "'" + columnMap.get(column) + "'";
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
					data += column + " = '" + Utilities.Sql.sanitizeSqlCharacterEscaping(columnMap.get(column)) + "'";
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
					primaries = primaries.substring(0, primaries.length() - 2);
				}
				return "CREATE TABLE " + sp.getArgumentValue(1) + " (" + cols + ", PRIMARY KEY (" + primaries + ") )";
			}

			private String columnToString(RequestArgumentStructureAssignment column) {
				String col = column.getArgument("colName").getValue() + " " + column.getArgument("type").getValue();
				switch ((String) column.getArgument("type").getValue()) {
					case "DATE":
					case "TIMESTAMP":
						break;
					case "VARCHAR":
						if (Integer.parseInt((String) column.getArgument("size").getValue()) < 0) {
							col += "(50)";
						} else {
							col += "(" + (String) column.getArgument("size").getValue() + ")";
						}
						break;
					case "INT":
						if (Integer.parseInt((String) column.getArgument("size").getValue()) < 0) {
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
