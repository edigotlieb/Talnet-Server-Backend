/**
 * FILE : DTDSelectRequest.java AUTHORS : Erez Gotlieb DESCRIPTION :
 */
package Request.DTDRequest;

import Request.Credentials;
import SQL.DynamicStatements.SqlQueryGenerator;
import SQL.DynamicStatements.SqlQueryGenerator.ORDER_ORIENTATION;
import SQL.SqlExecutor;
import SQL.Utilities.Utils;
import Statement.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DTDSelectRequest extends DTDRequest {

	Statement where;
	String orderBy;
	ORDER_ORIENTATION orie;

	public DTDSelectRequest(String tableName, Statement where, String orderBy, ORDER_ORIENTATION orie, Credentials creds) {
		super(creds, tableName);
		this.where = where;
		this.orderBy = orderBy;
		this.orie = orie;
	}

	public DTDSelectRequest(String tableName, Statement where, Credentials creds) {
		this(tableName, where, "", null, creds);
	}

	public boolean validateOpernads() {
		return this.where.validateOperands() && (this.orderBy.length() == 0 || Utils.isAlphaNumeric(this.orderBy));
	}

	@Override
	public DTD_ACTION_TYPE getActionType() {
		return DTD_ACTION_TYPE.SELECT;
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
