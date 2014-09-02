/**
 * FILE : DTDRequest.java AUTHORS : Erez Gotlieb DESCRIPTION :
 */
package Request.DTDRequest;

import Request.Credentials;
import Request.Exceptions.ValidationException;
import Request.Request;
import SQL.SqlExecutor;
import SQL.Utilities.ExistenceValidator;
import java.sql.SQLException;

public abstract class DTDRequest extends Request {

	protected String tableName;

	public DTDRequest(Credentials creds, String tableName) {
		super(creds);
		this.tableName = this.creds.getAppName() + "_" + tableName;
	}

	public enum DTD_ACTION_TYPE {

		SELECT, INSERT, UPDATE, DELETE, PUBLIC_SELECT
	}

	@Override
	public final Request.TYPE getType() {
		return Request.TYPE.DTD;
	}

	public abstract DTD_ACTION_TYPE getActionType();
	
	public abstract boolean santisizeData();

	@Override
	protected boolean CheckPermissions(SqlExecutor sqlExc) throws SQLException, ValidationException {
		if (!SQL.Utilities.Utils.isAlphaNumeric(tableName) || !this.santisizeData()) {
			throw new ValidationException(15);
		}
				
		if(!ExistenceValidator.isTableByName(sqlExc, tableName)){
			throw new ValidationException(12);
		}

		if (!this.creds.getTablePermissionList(sqlExc, this.tableName).contains(this.getActionType())) {
			System.out.println(this.creds.getTablePermissionList(sqlExc, this.tableName).toString());
			System.out.println(this.getActionType());
			throw new ValidationException(6);
		}

		if (this.getActionType() == DTD_ACTION_TYPE.SELECT) {
			DTDSelectRequest temp = (DTDSelectRequest) this;
			if (!temp.validateOpernads()) {
				throw new ValidationException(19);
			}
		}
		return true;
	}
}
