/**
 * FILE : SetPermissionGroupAdminRequest.java AUTHORS : Erez Gotlieb DESCRIPTION
 * :
 */
package Request.AppRequest;

import Request.Credentials;
import Request.Exceptions.ValidationException;
import SQL.PreparedStatements.StatementPreparer;
import SQL.SqlExecutor;
import SQL.Utilities.ExistenceValidator;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SetPermissionGroupAdminRequest extends AppRequest {

	String username, groupName;

	public SetPermissionGroupAdminRequest(String username, String groupName, Credentials creds) {
		super(creds);
		this.username = username;
		this.groupName = groupName;
	}

	@Override
	public APP_ACTION_TYPE getActionType() {
		return APP_ACTION_TYPE.SET_PERMISSIONGROUP_ADMIN;
	}

	@Override
	protected boolean CheckPermissions(SqlExecutor sqlExc) throws SQLException, ValidationException {
		// check if the user is the current permission group admin
		if (!ExistenceValidator.isUserByUsername(sqlExc, username)) {
			throw new ValidationException(3);
		}
		String curGroupAdmin = ExistenceValidator.permissionGroupByName(sqlExc, groupName);
		if (curGroupAdmin.length() == 0) {
			throw new ValidationException(9);
		}

		if (!this.creds.getUsername().equals(curGroupAdmin)) {
			throw new ValidationException(6);
		}

		if (!ExistenceValidator.isUserInGroup(sqlExc, username, groupName)) {
			throw new ValidationException(20);
		}

		return true;
	}

	@Override
	protected ResultSet performRequest(SqlExecutor sqlExc) throws SQLException {
		final String user_name = this.username;
		final String group_name = this.groupName;
		sqlExc.executePreparedStatement("SetPermissionGroupAdmin", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, user_name);
				ps.setString(2, group_name);
			}
		});
		return null;
	}
}
