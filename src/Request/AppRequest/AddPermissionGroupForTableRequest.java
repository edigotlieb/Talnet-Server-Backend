/**
 * FILE : AddPermissionGroupForTableRequest.java AUTHORS : Erez Gotlieb
 * DESCRIPTION :
 */
package Request.AppRequest;

import Request.AppRequest.Permission.PERMISSION_TYPE;
import Request.Credentials;
import Request.DTDRequest.DTDRequest;
import Request.Exceptions.ValidationException;
import SQL.PreparedStatements.StatementPreparer;
import SQL.SqlExecutor;
import SQL.Utilities.ExistenceValidator;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddPermissionGroupForTableRequest extends AppRequest {

	Permission permissionToAdd;

	public AddPermissionGroupForTableRequest(PERMISSION_TYPE type, String appName, String tableName, String permissionGroup, Credentials creds) {
		super(creds);
		this.permissionToAdd = new Permission(type, appName, appName + "_" + tableName, permissionGroup);
	}

	@Override
	public APP_ACTION_TYPE getActionType() {
		return APP_ACTION_TYPE.ADD_PERMISSION_GROUP_FOR_TABLE;
	}

	@Override
	protected boolean CheckPermissions(SqlExecutor sqlExc) throws SQLException, ValidationException {
		if (!this.creds.isAppSuperAdmin(this.permissionToAdd.getAppName())) {
			throw new ValidationException(6);
		}

		//check appName exists
		if (!ExistenceValidator.isAppByName(sqlExc, this.permissionToAdd.getAppName())) {
			throw new ValidationException(1);
		}

		//check table name exists
		if (!ExistenceValidator.isTableByName(sqlExc, this.permissionToAdd.getTableName())) {
			throw new ValidationException(12);
		}
		//check permission group exists
		if (!ExistenceValidator.isPermissionGroupByName(sqlExc, this.permissionToAdd.getPermissionGroup())) {
			throw new ValidationException(11);
		}

		if(SQL.Utilities.Utils.getGroupPermissionsForTable(sqlExc, this.permissionToAdd.getTableName(), this.permissionToAdd.getPermissionGroup()).contains(this.permissionToAdd.getType())){
			throw new ValidationException(22);
		}

		return true;
	}

	@Override
	protected ResultSet performRequest(SqlExecutor sqlExc) throws SQLException {
		final String app_name = this.permissionToAdd.getAppName();
		final String table_name = this.permissionToAdd.getTableName();
		final String permission_type = this.permissionToAdd.getType().toString();
		final String permission_name = this.permissionToAdd.getPermissionGroup();
		sqlExc.executePreparedStatement("AddPermissionGroupForTable", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, app_name);
				ps.setString(2, table_name);
				ps.setString(3, permission_type);
				ps.setString(4, permission_name);
			}
		});
		return null;
	}
}
