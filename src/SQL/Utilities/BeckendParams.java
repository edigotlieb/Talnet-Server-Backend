/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SQL.Utilities;

import SQL.PreparedStatements.StatementPreparer;
import SQL.SqlExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author t7639192
 */
public final class BeckendParams {
	
	public static ResultSet getParamResultSet(SqlExecutor sqlExc, String name) throws SQLException{
		final String param_name = name;
		ResultSet rset = sqlExc.executePreparedStatement("getBeckendParamValue", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, param_name);
			}
		});
		if (!rset.next()) {
			throw new IllegalArgumentException();
		}
		return rset;
	}
	
	public static int getInt(SqlExecutor sqlExc, String name) throws SQLException{
		return getParamResultSet(sqlExc, name).getInt("value");
	}
	
	public static String getString(SqlExecutor sqlExc, String name) throws SQLException{
		return getParamResultSet(sqlExc, name).getString("value");
	}
	
	public static boolean isRegistrationActivated(SqlExecutor sqlExc) throws SQLException{
		return getInt(sqlExc, "register_activated") > 0;
	}
}
