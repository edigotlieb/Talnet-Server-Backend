/**
 * FILE : SignInRequest.java AUTHORS : Erez Gotlieb DESCRIPTION :
 */
package Request.UserRequest;

import Request.Credentials;
import SQL.PreparedStatements.StatementPreparer;
import SQL.SqlExecutor;
import Utilities.Hashing;
import Utilities.RuntimeParams;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserSigninRequest extends UserRequest {

	final String requesting_app;
	
	public UserSigninRequest(String requesting_app, Credentials creds) {
		super(creds);
		this.requesting_app = requesting_app;
	}

	@Override
	public USER_ACTION_TYPE getActionType() {
		return USER_ACTION_TYPE.SIGN_IN;
	}

	@Override
	protected boolean CheckPermissions(SqlExecutor sqlExc) {
		return true;
	}

	@Override
	protected ResultSet performRequest(SqlExecutor sqlExc) throws SQLException {
		final String username = this.creds.getUsername();
		final String appname = this.creds.getAppName();
		
		ResultSet rset1 = sqlExc.executePreparedStatement("GetKey", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, username);
				ps.setString(2, requesting_app);
			}
		});                
		if (!rset1.next()) {
			final String temp_key = Hashing.generateChallenge();
			final int tk_expire_time_interval_min = (int) RuntimeParams.getParams("keyLifeTime");
			sqlExc.executePreparedStatement("AddTempKey", new StatementPreparer() {
				@Override
				public void prepareStatement(PreparedStatement ps) throws SQLException {
					ps.setString(1, username);
					ps.setString(2, requesting_app);
					ps.setString(3, temp_key);
					ps.setInt(4, tk_expire_time_interval_min);
				}
			});
		}

		return sqlExc.executePreparedStatement("getAllUserInfoByUsernameNoPass", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, username);
				ps.setString(2, requesting_app);
			}
		});
	}
}
