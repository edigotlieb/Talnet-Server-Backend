/**
 * FILE : UserAddPermissionGroupRequest.java AUTHORS : Erez Gotlieb DESCRIPTION
 * :
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

public class UserAddPermissionGroupRequest extends UserRequest {

	String userToAddTo, groupName;

	public UserAddPermissionGroupRequest(String userToAddTo, String groupName, Credentials creds) {
		super(creds);
		this.userToAddTo = userToAddTo;
		this.groupName = groupName;
	}

	@Override
	public USER_ACTION_TYPE getActionType() {
		return USER_ACTION_TYPE.ADD_PERMISSION;
	}

	@Override
	protected boolean CheckPermissions(SqlExecutor sqlExc) throws SQLException, ValidationException {
		if (this.userToAddTo.equals(Credentials.anonymous)) {
			throw new ValidationException(3);
		}

		// check user and group exists
		if (!ExistenceValidator.isUserByUsername(sqlExc, this.userToAddTo)) {
			throw new ValidationException(3);
		}

		String groupadmin = ExistenceValidator.permissionGroupByName(sqlExc, this.groupName);
		if (groupadmin.length() == 0) {
			throw new ValidationException(11);
		}
		// check if adder is group admin
		if (!groupadmin.equals(creds.getUsername())) {
			throw new ValidationException(6);
		}
		
		final String username = this.userToAddTo;
		ResultSet rs = sqlExc.executePreparedStatement("getUserPermissionGroupsNoPass", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, username);
			}
		});
		while(rs.next()){
			if(rs.getString("PERMISSION_NAME").equals(this.groupName)){
				throw new ValidationException(27);
			}
		}
		
		return true;
	}

	@Override
	protected ResultSet performRequest(SqlExecutor sqlExc) throws SQLException {
		final String username = this.userToAddTo;
		final String permission_name = this.groupName;
		sqlExc.executePreparedStatement("AddUserPermissionGroup", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, username);
				ps.setString(2, permission_name);
			}
		});
		return null;
	}
}
