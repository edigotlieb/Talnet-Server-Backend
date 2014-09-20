/**
 * FILE : UserSelectRequest.java AUTHORS : Erez Gotlieb DESCRIPTION :
 */
package Request.UserRequest;

import Request.Credentials;
import Request.Exceptions.ValidationException;
import SQL.DynamicStatements.SqlQueryGenerator;
import SQL.SqlExecutor;
import SQL.Utilities.Utils;
import Statement.AndStatement;
import Statement.RelStatement;
import Statement.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserSelectRequest extends UserRequest {

	Statement where;
	public static final String userTable = "USERS";
	private static final String passwordField = "PASSWORD";
	private static final String usernameField = "USERNAME";

	public UserSelectRequest(Statement where, Credentials creds) {
		this(creds);
		this.where = where;
	}

	public UserSelectRequest(Credentials creds) {
		super(creds);
	}

	@Override
	public USER_ACTION_TYPE getActionType() {
		return USER_ACTION_TYPE.SELECT;
	}

	@Override
	protected boolean CheckPermissions(SqlExecutor sqlExc) throws SQLException, ValidationException {
		if (this.where.isColumnIn(passwordField)) {
			throw new ValidationException(18);
		}
		if (!this.where.validateOperands()) {
			throw new ValidationException(19);
		}
		if(!this.creds.isInPermissionGroup("User")) {
				throw new ValidationException(32);
		}
		return true;
	}

	@Override
	protected ResultSet performRequest(SqlExecutor sqlExc) throws SQLException {

		List<String> userCols = Utils.getColNames(sqlExc, userTable);
		userCols.remove(passwordField);
		userCols.add("CONCAT_WS(' ',USERS.FIRST_NAME,USERS.LAST_NAME) AS NAME");
		Statement whereNoAnon = new AndStatement(this.where, new RelStatement(usernameField,  Credentials.anonymous, "!="));
		return sqlExc.executeDynamicStatementQry(SqlQueryGenerator.select(userCols, userTable, whereNoAnon));
	}
}
