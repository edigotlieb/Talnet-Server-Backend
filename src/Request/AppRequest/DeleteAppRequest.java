/**
 * FILE : SignInRequest.java AUTHORS : Erez Gotlieb DESCRIPTION :
 */
package Request.AppRequest;

import Request.Credentials;
import Request.Exceptions.ValidationException;
import SQL.DynamicStatements.SqlQueryGenerator;
import SQL.PreparedStatements.StatementPreparer;
import SQL.SqlExecutor;
import SQL.Utilities.ExistenceValidator;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteAppRequest extends AppRequest {

	String appName;

	public DeleteAppRequest(Credentials creds, String appName) {
		super(creds);
		this.appName = appName;
	}

	@Override
	protected boolean CheckPermissions(SqlExecutor sqlExc) throws SQLException, ValidationException {
		if (!ExistenceValidator.isAppByName(sqlExc, appName)) {
			throw new ValidationException(1);
		}
		if (this.appName.equals(Credentials.masterAppName)) {
			throw new ValidationException(18);
		}
		if (!this.creds.isAppSuperAdmin(this.appName)) {
			throw new ValidationException(6);
		}
		return true;
	}

	@Override
	public APP_ACTION_TYPE getActionType() {
		return AppRequest.APP_ACTION_TYPE.DELETE_APP;
	}

	@Override
	protected ResultSet performRequest(SqlExecutor sqlExc) throws SQLException {
		//deleteAppPermissions
		final String app_name = this.appName;
		sqlExc.executePreparedStatement("DeleteAppPermissionsByAppName", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, app_name);
			}
		});
		//dropAppTables
		ResultSet rset = sqlExc.executePreparedStatement("getAppTables", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, app_name);
			}
		});
		while (rset.next()) {
			sqlExc.executeDynamicStatementQry(SqlQueryGenerator.drop(app_name + "_" + rset.getString("TABLENAME")));
		}
		//deleteAppTables
		sqlExc.executePreparedStatement("DeleteAppTables", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, app_name);
			}
		});
		//deletePermissionUsers
		final String permission_appadmin = this.appName + "_" + Credentials.appAdminSuffix;
		sqlExc.executePreparedStatement("DeletePermissionUsers", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, permission_appadmin);
			}
		});
		//detelePermission
		sqlExc.executePreparedStatement("DeletePermission", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, permission_appadmin);
			}
		});
		//deleteApp
		sqlExc.executePreparedStatement("DeleteApp", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, app_name);
			}
		});
		return null;
	}
}
