/**
 * FILE : BackendParams.java AUTHORS : Idan Berkovits
 */
package Utilities;

import SQL.PreparedStatements.StatementPreparer;
import SQL.SqlExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * this class handles all backendParams
 *
 * @author idanb55
 */
public abstract class BackendParams {

	private static Map<String, String> params;

	/**
	 * loads the parameters into the params map
	 *
	 * @param sqlExc the sql executor to use
	 * @throws SQLException thrown in case of sql executing exception
	 */
	public static void loadParams(SqlExecutor sqlExc) throws SQLException {
		params = new HashMap<>();
		ResultSet rset = sqlExc.executePreparedStatement("getBackendParams", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
			}
		});
		while (rset.next()) {
			params.put(rset.getString("name"), rset.getString("value"));
		}
	}

	/**
	 * returns a set of the parameter names
	 *
	 * @return a set of the parameter names
	 */
	public static Set<String> getNames() {
		return params.keySet();
	}

	/**
	 * returns a parameter as string
	 *
	 * @param name the name of the parameter to return
	 * @return the parameter value
	 */
	public static String getParameter(String name) {
		return params.get(name);
	}
}
