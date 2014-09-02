/**
 * FILE : DTDSelectRequest.java AUTHORS : Erez Gotlieb DESCRIPTION :
 */
package Request.DTDRequest;

import Request.Credentials;
import Request.Exceptions.ValidationException;
import SQL.DynamicStatements.SqlQueryGenerator;
import SQL.DynamicStatements.SqlQueryGenerator.ORDER_ORIENTATION;
import SQL.SqlExecutor;
import SQL.Utilities.ExistenceValidator;
import SQL.Utilities.Utils;
import Statement.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DTDPublicSelectRequest extends DTDSelectRequest {

	private String appName;

	public DTDPublicSelectRequest(String appName, String tableName, Statement where, String orderBy, ORDER_ORIENTATION orie, Credentials creds) {
		super(tableName, where, orderBy, orie, creds);
		this.appName = appName;
		this.tableName = this.appName + "_" + tableName;
	}

	public DTDPublicSelectRequest(String appName, String tableName, Statement where, Credentials creds) {
		this(appName, tableName, where, "", null, creds);
	}

	@Override
	public boolean validateOpernads() {
		return this.where.validateOperands() && (this.orderBy.length() == 0 || Utils.isAlphaNumeric(this.orderBy));
	}

	@Override
	protected boolean CheckPermissions(SqlExecutor sqlExc) throws SQLException, ValidationException {
		if (!SQL.Utilities.Utils.isAlphaNumeric(tableName)) {
			throw new ValidationException(15);
		}
		
		if(!ExistenceValidator.isTableByName(sqlExc, tableName)){
			throw new ValidationException(12);
		}

		if (!this.creds.getTablePublicPermission(sqlExc, this.tableName)) {
			throw new ValidationException(6);
		}

		if (!this.validateOpernads()) {
			throw new ValidationException(19);
		}
		return true;
	}

	@Override
	public DTD_ACTION_TYPE getActionType() {
		return DTD_ACTION_TYPE.PUBLIC_SELECT;
	}

	@Override
	protected ResultSet performRequest(SqlExecutor sqlExc) throws SQLException {
		return sqlExc.executeDynamicStatementQry(SqlQueryGenerator.select(null, tableName, where, orderBy, orie));
	}

	@Override
	public boolean santisizeData() {
		return true;
	}
}
