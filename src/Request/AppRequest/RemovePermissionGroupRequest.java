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

public class RemovePermissionGroupRequest extends AppRequest {

	private final String permissionGroupName;

	public RemovePermissionGroupRequest(Credentials creds, String permissionGroupName) {
		super(creds);
		this.permissionGroupName = permissionGroupName;
	}

	@Override
	protected boolean CheckPermissions(SqlExecutor sqlExc) throws SQLException, ValidationException {
		String username = ExistenceValidator.permissionGroupByName(sqlExc, this.permissionGroupName);
		if (username.length() == 0) {
			throw new ValidationException(9);
		}
		if (!this.creds.getUsername().equals(username)) {
			throw new ValidationException(6);
		}
		if(Credentials.isSpecialPermissionGroup(permissionGroupName)){
			throw new ValidationException(17);
		}

		return true;
	}

	@Override
	public APP_ACTION_TYPE getActionType() {
		return AppRequest.APP_ACTION_TYPE.REMOVE_PERMISSIONGROUP;
	}

	@Override
	protected ResultSet performRequest(SqlExecutor sqlExc) throws SQLException {
		//delete userpermission relation
		final String app_name = this.permissionGroupName;
		sqlExc.executePreparedStatement("DeletePermissionUsers", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, app_name);
			}
		});
		//delete app permission
		sqlExc.executePreparedStatement("DeleteAppPermissionsByPermissionName", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, app_name);
			}
		});
		//delete permission
		sqlExc.executePreparedStatement("DeletePermission", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, app_name);
			}
		});
		return null;
	}
}
