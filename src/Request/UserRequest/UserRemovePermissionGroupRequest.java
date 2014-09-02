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

public class UserRemovePermissionGroupRequest extends UserRequest {

	String userToRemoveFrom, groupToRemove;

	public UserRemovePermissionGroupRequest(String userToRemoveFrom, String groupToRemove, Credentials creds) {
		this(creds);
		this.userToRemoveFrom = userToRemoveFrom;
		this.groupToRemove = groupToRemove;
	}

	public UserRemovePermissionGroupRequest(Credentials creds) {
		super(creds);
	}
        
	@Override
	public USER_ACTION_TYPE getActionType() {
		return USER_ACTION_TYPE.REMOVE_PERMISSION;
	}

	@Override
	protected boolean CheckPermissions(SqlExecutor sqlExc) throws SQLException, ValidationException {
		// check user and group exists
		String groupAdmin = ExistenceValidator.permissionGroupByName(sqlExc, this.groupToRemove);
		if (groupAdmin.length() == 0) {
			throw new ValidationException(11);
		}
                
                if(this.groupToRemove.equals(Credentials.userPermission)){
                    throw new ValidationException(26);
                }

		if (this.userToRemoveFrom.length() > 0) {
			if (!ExistenceValidator.isUserByUsername(sqlExc, this.userToRemoveFrom)) {
				throw new ValidationException(3);
			}

			// check if adder is super admin or group admin
			if (!groupAdmin.equals(creds.getUsername()) && !this.userToRemoveFrom.equals(creds.getUsername())) {
				throw new ValidationException(6);
			}
		} else {
			this.userToRemoveFrom = creds.getUsername();
			if (groupAdmin.equals(creds.getUsername())) {
				throw new ValidationException(16);
			}
		}

		return true;
	}

	@Override
	protected ResultSet performRequest(SqlExecutor sqlExc) throws SQLException {
		final String username = this.userToRemoveFrom;
		final String permission_name = this.groupToRemove;
		sqlExc.executePreparedStatement("RemoveUserPermission", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(2, username);
				ps.setString(1, permission_name);
			}
		});
		return null;
	}
}
