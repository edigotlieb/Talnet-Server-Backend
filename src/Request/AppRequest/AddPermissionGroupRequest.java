/**
 * FILE : SignInRequest.java AUTHORS : Erez Gotlieb DESCRIPTION :
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

public class AddPermissionGroupRequest extends AppRequest {

	private final String permissionGroupName;
	private final String description;

	public AddPermissionGroupRequest(Credentials creds, String permissionGroupName, String description) {
		super(creds);
		this.permissionGroupName = permissionGroupName;
		this.description = description;
	}

	@Override
	protected boolean CheckPermissions(SqlExecutor sqlExc) throws SQLException, ValidationException {
		if (ExistenceValidator.isPermissionGroupByName(sqlExc, this.permissionGroupName) || permissionGroupName.equals(Credentials.anonymous)) {
			throw new ValidationException(5);
		}
		if (Credentials.isSpecialPermissionGroup(permissionGroupName)) {
			throw new ValidationException(17);
		}
		if (!this.creds.isDeveloper()) {
			throw new ValidationException(6);
		}
		return true;
	}

	@Override
	public APP_ACTION_TYPE getActionType() {
		return AppRequest.APP_ACTION_TYPE.ADD_PERMISSIONGROUP;
	}

	@Override
	protected ResultSet performRequest(SqlExecutor sqlExc) throws SQLException {
		final String permission_name = this.permissionGroupName;
		final String permission_desc = this.description;
		final String username = this.creds.getUsername();
		sqlExc.executePreparedStatement("AddPermissionGroup", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, permission_name);
				ps.setString(2, permission_desc);
				ps.setString(3, username);
			}
		});
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
