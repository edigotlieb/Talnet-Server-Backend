/**
 * FILE : DTDUpdateRequest.java AUTHORS : Erez Gotlieb DESCRIPTION :
 */
package Request.DTDRequest;

import Request.Credentials;
import SQL.DynamicStatements.SqlQueryGenerator;
import SQL.SqlExecutor;
import SQL.Utilities.Utils;
import Statement.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

public class DTDUpdateRequest extends DTDRequest {

	Statement where;
	Map<String, String> data;

	public DTDUpdateRequest(String tableName, Statement where, Map<String, String> data, Credentials creds) {
		super(creds, tableName);
		this.where = where;
		this.data = data;
	}

	@Override
	public DTD_ACTION_TYPE getActionType() {
		return DTD_ACTION_TYPE.UPDATE;
	}

	@Override
	protected ResultSet performRequest(SqlExecutor sqlExc) throws SQLException {
		sqlExc.executeDynamicStatementQry(SqlQueryGenerator.update(tableName, data, where));
		return null;
	}
	
		@Override
	public boolean santisizeData() {
		Iterator<String> iterator = data.keySet().iterator();
		while (iterator.hasNext()) {
			String col = iterator.next();
			if(!Utils.isAlphaNumeric(col)){
				return false;
			}
			data.put(col, Utils.sanitizeSqlCharacterEscaping(data.get(col)));
		}
		return true;
	}
}
