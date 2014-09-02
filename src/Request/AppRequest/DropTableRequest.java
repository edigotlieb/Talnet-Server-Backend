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

public class DropTableRequest extends AppRequest {

	private String tableName;
	private String appName;

	public DropTableRequest(Credentials creds, String tableName, String appName) {
		super(creds);
		this.appName = appName;
		this.tableName = this.appName + "_" + tableName;
	}

	@Override
	protected boolean CheckPermissions(SqlExecutor sqlExc) throws SQLException, ValidationException {
		if (!ExistenceValidator.isTableByName(sqlExc, this.tableName)) {
			throw new ValidationException(8);
		}
		if (!this.creds.isAppSuperAdmin(this.appName)) {
			throw new ValidationException(6);
		}
		return true;
	}

	@Override
	public APP_ACTION_TYPE getActionType() {
		return AppRequest.APP_ACTION_TYPE.DROP_TABLE;
	}

	@Override
	protected ResultSet performRequest(SqlExecutor sqlExc) throws SQLException {
		final String table_name = this.tableName;
		//drop table
		sqlExc.executeDynamicStatementQry(SqlQueryGenerator.drop(table_name));
		//delete user permissions
		sqlExc.executePreparedStatement("DeleteAppPermissionsByTableName", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, table_name);
			}
		});
		//delete table
		sqlExc.executePreparedStatement("DeleteTable", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, table_name);
			}
		});
		return null;
	}
}
