/**
 * FILE : UserUpdateUserPasswordRequest.java AUTHORS : Erez Gotlieb DESCRIPTION
 * :
 */
package Request.UserRequest;

import Request.Credentials;
import Request.Exceptions.ValidationException;
import SQL.PreparedStatements.StatementPreparer;
import SQL.SqlExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDeleteUserRequest extends UserRequest {

	String userToDelete;

	public UserDeleteUserRequest(Credentials creds) {
		super(creds);
	}

	public UserDeleteUserRequest(Credentials creds, String userToDelete) {
		this(creds);
		this.userToDelete = userToDelete;
	}

	@Override
	public USER_ACTION_TYPE getActionType() {
		return USER_ACTION_TYPE.DELETE_USER;
	}

	@Override
	protected boolean CheckPermissions(SqlExecutor sqlExc) throws SQLException, ValidationException {
		if (this.userToDelete.equals(Credentials.anonymous)
				|| !SQL.Utilities.ExistenceValidator.isUserByUsername(sqlExc, userToDelete)) {
			throw new ValidationException(3);
		}

		if (!this.creds.getUsername().equals(this.userToDelete)) {
			throw new ValidationException(6);
		}

		final String username = this.userToDelete;
		ResultSet rs = sqlExc.executePreparedStatement("getUserPermissionGroupsAdmin", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, username);
			}
		});
		if (rs.next()) {
			throw new ValidationException(24);
		}
		return true;
	}

	@Override
	protected ResultSet performRequest(SqlExecutor sqlExc) throws SQLException {
		final String username = this.userToDelete;
		sqlExc.executePreparedStatement("DeletePermissionUsersByUser", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, username);
			}
		});
		sqlExc.executePreparedStatement("DeleteUser", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, username);
			}
		});
		return null;
	}
}
