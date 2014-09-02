/**
 * FILE : DTDDeleteRequest.java AUTHORS : Erez Gotlieb DESCRIPTION :
 */
package Request.DTDRequest;

import Request.Credentials;
import Request.Exceptions.ValidationException;
import SQL.DynamicStatements.SqlQueryGenerator;
import SQL.SqlExecutor;
import Statement.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DTDDeleteRequest extends DTDRequest {

	Statement where;

	public DTDDeleteRequest(String tableName, Statement where, Credentials creds) {
		super(creds, tableName);
		this.where = where;
	}

	@Override
	public DTD_ACTION_TYPE getActionType() {
		return DTD_ACTION_TYPE.DELETE;
	}

	@Override
	protected ResultSet performRequest(SqlExecutor sqlExc) throws SQLException {
		sqlExc.executeDynamicStatementQry(SqlQueryGenerator.delete(this.tableName, this.where));
		return null;
	}

	@Override
	public boolean santisizeData() {
		return true;
	}
}
