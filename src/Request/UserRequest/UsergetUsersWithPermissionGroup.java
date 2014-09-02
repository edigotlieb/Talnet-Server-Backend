/**
 * FILE : UserRemovePermissionGroupRequest.java AUTHORS : Erez Gotlieb
 * DESCRIPTION :
 */
package Request.UserRequest;

import Request.Credentials;
import Request.Exceptions.ValidationException;
import SQL.PreparedStatements.StatementPreparer;
import SQL.SqlExecutor;
import SQL.Utilities.ExistenceValidator;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsergetUsersWithPermissionGroup extends UserRequest {

	String groupName;

	public UsergetUsersWithPermissionGroup(String groupName, Credentials creds) {
		this(creds);
		this.groupName = groupName;
	}

	public UsergetUsersWithPermissionGroup(Credentials creds) {
		super(creds);
	}

	@Override
	public USER_ACTION_TYPE getActionType() {
		return USER_ACTION_TYPE.GET_USERS_WITH_GROUPS;
	}

	@Override
	protected boolean CheckPermissions(SqlExecutor sqlExc) throws SQLException, ValidationException {
		// check user and group exists
		String groupAdmin = ExistenceValidator.permissionGroupByName(sqlExc, this.groupName);
		if (groupAdmin.length() == 0) {
			throw new ValidationException(11);
		}

		// check if user is super admin or group admin
		if (!groupAdmin.equals(this.creds.getUsername())) {
			throw new ValidationException(6);
		}

		return true;
	}

	@Override
	protected ResultSet performRequest(SqlExecutor sqlExc) throws SQLException {
		final String permission_name = this.groupName;
		return sqlExc.executePreparedStatement("getUserInfoWithPermissionGroup", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, permission_name);
			}
		});
	}
}
