/**
 * FILE : CreateAppRequest.java AUTHORS : Erez Gotlieb DESCRIPTION :
 */
package Request.AppRequest;

import Request.Credentials;
import Request.Exceptions.ValidationException;
import SQL.PreparedStatements.StatementPreparer;
import SQL.SqlExecutor;
import SQL.Utilities.ExistenceValidator;
import SQL.Utilities.Utils;
import Utilities.Hashing;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreateAppRequest extends AppRequest {

	String appName;
	String appKey;

	public CreateAppRequest(Credentials creds, String appName, String appKey) {
		super(creds);
		this.appName = appName;
		this.appKey = appKey;
	}

	@Override
	public APP_ACTION_TYPE getActionType() {
		return AppRequest.APP_ACTION_TYPE.CREATE_APP;
	}

	@Override
	protected boolean CheckPermissions(SqlExecutor sqlExc) throws SQLException, ValidationException {
		// user is devel
		if (!this.creds.isDeveloper()) {
			throw new ValidationException(6);
		}

		// no such app name yet
		if (ExistenceValidator.isAppByName(sqlExc, appName)) {
			throw new ValidationException(14);
		}
		if (Credentials.isSpecialAppName(appName)) {
			throw new ValidationException(25);
		}

		// check specific app_id
		if (!this.creds.isMasterApplication()) {
			throw new ValidationException(13);
		}

		if(!this.appName.matches("[a-zA-Z][\\w]*")){
			throw new ValidationException(29);
		}

		return true;
	}

	@Override
	protected ResultSet performRequest(SqlExecutor sqlExc) throws SQLException {
		//add to APPS
		final String app_name = this.appName;
		final String hashedAppKey = Hashing.MD5Hash(appKey);
		//add app_admin permission
		final String permission_name = this.appName + "_admin";
		final String permission_desc = "The admin user of the '" + this.appName + "' application";
		final String permission_admin_username = this.creds.getUsername();
		sqlExc.executePreparedStatement("AddPermissionGroup", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, permission_name);
				ps.setString(2, permission_desc);
				ps.setString(3, permission_admin_username);
			}
		});
		sqlExc.executePreparedStatement("AddApp", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, app_name);
				ps.setString(2, hashedAppKey);
				ps.setString(3, permission_name);
			}
		});
		//set current user to be app_admin
		//add app_admin permission
		sqlExc.executePreparedStatement("AddUserPermissionGroup", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, permission_admin_username);
				ps.setString(2, permission_name);
			}
		});
		return null;
	}
}
