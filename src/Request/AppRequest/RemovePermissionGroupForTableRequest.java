/**
 * FILE : RemovePermissionGroupForTableRequest.java AUTHORS : Erez Gotlieb
 * DESCRIPTION :
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

public class RemovePermissionGroupForTableRequest extends AppRequest {

	Permission toRemove;

	public RemovePermissionGroupForTableRequest(Permission.PERMISSION_TYPE type, String appName, String tableName, String gorupName, Credentials creds) {
		super(creds);
		this.toRemove = new Permission(type, appName, appName + "_" + tableName, gorupName);
	}

	@Override
	public APP_ACTION_TYPE getActionType() {
		return APP_ACTION_TYPE.REMOVE_PERMISSION_GROUP_FOR_TABLE;
	}

	@Override
	protected boolean CheckPermissions(SqlExecutor sqlExc) throws SQLException, ValidationException {
		//check appName exists
		if (!ExistenceValidator.isAppByName(sqlExc, this.toRemove.getAppName())) {
			throw new ValidationException(1);
		}

		if (!this.creds.isAppSuperAdmin(this.toRemove.getAppName())) {
			throw new ValidationException(6);
		}

		//check table name exists
		if (!ExistenceValidator.isTableByName(sqlExc, this.toRemove.getTableName())) {
			throw new ValidationException(12);
		}
		//check permission group exists
		if (!ExistenceValidator.isPermissionGroupByName(sqlExc, this.toRemove.getPermissionGroup())) {
			throw new ValidationException(11);
		}

		if (this.toRemove.getPermissionGroup().equals(this.toRemove.getAppName() + "_" + Credentials.appAdminSuffix)) {
			throw new ValidationException(23);
		}
		return true;
	}

	@Override
	protected ResultSet performRequest(SqlExecutor sqlExc) throws SQLException {
		final String table_name = this.toRemove.getTableName();
		final String group_name = this.toRemove.getPermissionGroup();
		final String permission_type = this.toRemove.getType().toString();
		sqlExc.executePreparedStatement("DeleteSingleAppPermission", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, table_name);
				ps.setString(2, group_name);
				ps.setString(3, permission_type);
			}
		});
		return null;
	}
}
