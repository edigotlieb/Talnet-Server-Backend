/**
 * FILE : DTDInsertRequest.java AUTHORS : Erez Gotlieb DESCRIPTION :
 */
package Request.DTDRequest;

import Request.Credentials;
import SQL.DynamicStatements.SqlQueryGenerator;
import SQL.SqlExecutor;
import SQL.Utilities.Utils;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

public class DTDInsertRequest extends DTDRequest {

	Map<String, String> data;

	public DTDInsertRequest(String tableName, Map<String, String> data, Credentials creds) {
		super(creds, tableName);
		this.data = data;
	}

	@Override
	public DTD_ACTION_TYPE getActionType() {
		return DTD_ACTION_TYPE.INSERT;
	}

	@Override
	protected ResultSet performRequest(SqlExecutor sqlExc) throws SQLException {
		return sqlExc.executeDynamicStatementQry(SqlQueryGenerator.insert(tableName, data));
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
