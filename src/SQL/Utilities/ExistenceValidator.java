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
public final class ExistenceValidator {

	//returns hashed password
	public static String userByUsername(SqlExecutor sqlExc, String username) throws SQLException {
		final String user_name = username;
		ResultSet rset = sqlExc.executePreparedStatement("getAllUserInfoByUsername", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, user_name);
			}
		});
		if (!rset.next()) {
			return "";
		}
		String result = rset.getString("PASSWORD");
		rset.close();
		return result;
	}

	public static boolean isUserByUsername(SqlExecutor sqlExc, String username) throws SQLException {
		return userByUsername(sqlExc, username).length() > 0;
	}

	//returns permissiongroup admin username
	public static String permissionGroupByName(SqlExecutor sqlExc, String groupName) throws SQLException {
		final String group_name = groupName;
		ResultSet rset = sqlExc.executePreparedStatement("getPermissionGroupInfoByName", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, group_name);
			}
		});
		if (!rset.next()) {
			return "";
		}
		String result = rset.getString("USERNAME");
		rset.close();
		return result;
	}

	public static boolean isPermissionGroupByName(SqlExecutor sqlExc, String groupName) throws SQLException {
		return permissionGroupByName(sqlExc, groupName).length() > 0;
	}

	//returns hashed app key
	public static String appByName(SqlExecutor sqlExc, String appName) throws SQLException {
		final String appname = appName;
		ResultSet rset = sqlExc.executePreparedStatement("getAllAppInfoByName", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, appname);
			}
		});
		if (!rset.next()) {
			return "";
		}
		String result = rset.getString("APP_KEY");
		rset.close();
		return result;
	}

	public static boolean isAppByName(SqlExecutor sqlExc, String appName) throws SQLException {
		return appByName(sqlExc, appName).length() > 0;
	}

	//returns table's app name
	public static String tableByName(SqlExecutor sqlExc, String tableName) throws SQLException {
		final String tablename = tableName;
		ResultSet rset = sqlExc.executePreparedStatement("getTableInfoByName", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, tablename);
			}
		});
		if (!rset.next()) {
			return "";
		}
		String result = rset.getString("APP_NAME");
		rset.close();
		return result;
	}

	public static boolean isTableByName(SqlExecutor sqlExc, String tableName) throws SQLException {
		return tableByName(sqlExc, tableName).length() > 0;
	}

	//returns table's app name
	public static boolean isUserInGroup(SqlExecutor sqlExc, String userName, String permissionGroup) throws SQLException {
		final String username = userName;
		final String permissiongroup = permissionGroup;
		ResultSet rset = sqlExc.executePreparedStatement("getUserPermissionRelation", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, username);
				ps.setString(2, permissiongroup);
			}
		});
		return rset.next();
	}
}
