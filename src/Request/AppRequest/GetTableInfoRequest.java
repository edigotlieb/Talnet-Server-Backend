/**
 * FILE : GetTablesInfo.java AUTHORS : Erez Gotlieb DESCRIPTION :
 */
package Request.AppRequest;

import Request.Credentials;
import Request.Exceptions.ValidationException;
import SQL.DynamicStatements.SqlQueryGenerator;
import SQL.SqlExecutor;
import SQL.Utilities.ExistenceValidator;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetTableInfoRequest extends AppRequest {

	String appName;
	String tableName;

	public GetTableInfoRequest(Credentials creds, String tableName, String appName) {
		super(creds);
		this.appName = appName;
		this.tableName = tableName;
	}

	@Override
	public APP_ACTION_TYPE getActionType() {
		return AppRequest.APP_ACTION_TYPE.GET_TABLE_INFO;
	}

	@Override
	protected boolean CheckPermissions(SqlExecutor sqlExc) throws SQLException, ValidationException {
		if (!ExistenceValidator.isAppByName(sqlExc, this.appName)) {
			throw new ValidationException(1);
		}
		
		if (!this.creds.isAppSuperAdmin(this.appName)) {
			throw new ValidationException(6);
		}
		
		if (!ExistenceValidator.isTableByName(sqlExc, this.appName + "_" + this.tableName)) {
			throw new ValidationException(8);
		}

		return true;
	}

	@Override
	protected ResultSet performRequest(SqlExecutor sqlExc) throws SQLException {
		return sqlExc.executeDynamicStatementQry(SqlQueryGenerator.desc(this.appName + "_" + this.tableName));
	}
}
