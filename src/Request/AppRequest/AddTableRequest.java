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
import SQL.Utilities.Utils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddTableRequest extends AppRequest {

	private final List<Column> columns;
	private final String tableName;
	private final String appName;

	public AddTableRequest(Credentials creds, String tableName, List<Column> cols, String appName) {
		super(creds);
		this.columns = new ArrayList<>(cols);
		this.tableName = tableName;
		this.appName = appName;
	}

	@Override
	protected boolean CheckPermissions(SqlExecutor sqlExc) throws SQLException, ValidationException {
		if (!this.creds.isAppSuperAdmin(appName)) {
			throw new ValidationException(6);
		}
		
		if (ExistenceValidator.isTableByName(sqlExc, this.appName + "_" + this.tableName)) {
			//table exists
			throw new ValidationException(7);
		}

		boolean primaryFlag = false;
		for (Column col : columns) {
			if (!Utils.isAlphaNumeric(col.getColName())) {
				throw new ValidationException(15);
			}
			if (col.isPrimary) {
				primaryFlag = true;
			}
		}

		if (!primaryFlag) {
			throw new ValidationException(21);
		}

		if(!this.appName.matches("[a-zA-Z][\\w]*")){
			throw new ValidationException(15);
		}
		return true;
	}

	@Override
	public APP_ACTION_TYPE getActionType() {
		return AppRequest.APP_ACTION_TYPE.ADD_TABLE;
	}

	@Override
	protected ResultSet performRequest(SqlExecutor sqlExc) throws SQLException {
		final String app_name = appName;
		final String table_name = app_name + "_" + tableName;
		sqlExc.executeDynamicStatementQry(SqlQueryGenerator.create(app_name + "_" + tableName, columns));
		sqlExc.executePreparedStatement("AddTable", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, table_name);
				ps.setString(2, app_name);
			}
		});

		for (Permission.PERMISSION_TYPE per : Permission.PERMISSION_TYPE.values()) {
			final String appname = this.appName;
			final String tablename = app_name + "_" + tableName;
			final String permission_type = per.toString();
			final String permission_name = this.appName + "_" + Credentials.appAdminSuffix;
			sqlExc.executePreparedStatement("AddPermissionGroupForTable", new StatementPreparer() {
				@Override
				public void prepareStatement(PreparedStatement ps) throws SQLException {
					ps.setString(1, appname);
					ps.setString(2, tablename);
					ps.setString(3, permission_type);
					ps.setString(4, permission_name);
				}
			});
		}

		return null;
	}
}
