/**
 * FILE : UserSignupRequest.java AUTHORS : Erez Gotlieb DESCRIPTION :
 */
package Request.UserRequest;

import Request.Credentials;
import Request.Exceptions.ValidationException;
import SQL.PreparedStatements.StatementPreparer;
import SQL.SqlExecutor;
import SQL.Utilities.BeckendParams;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserSignupRequest extends UserRequest {

	String user, pass, name, disp_name, email, phonePre, phoneSuf;
	int room, year;

	public UserSignupRequest(String user, String pass, String name, String disp_name, String email, int room, int year, String phonePre, String phoneSuf, Credentials creds) {
		this(creds);
		this.user = user;
		this.pass = pass;
		this.name = name;
		this.disp_name = disp_name;
		this.email = email;
		this.room = room;
		this.year = year;
		this.phonePre = phonePre;
		this.phoneSuf = phoneSuf;
	}

	public UserSignupRequest(Credentials creds) {
		super(creds);
	}

	@Override
	public USER_ACTION_TYPE getActionType() {
		return USER_ACTION_TYPE.SIGN_UP;
	}

	@Override
	protected boolean CheckPermissions(SqlExecutor sqlExc) throws SQLException, ValidationException {
		if(!BeckendParams.isRegistrationActivated(sqlExc)){
			throw new ValidationException(31);
		}
		
		if (!this.creds.isAnonymous()) {
			throw new ValidationException(10);
		}
		//check specific appkey
		if (!this.creds.isMasterApplication()) {
			throw new ValidationException(13);
		}
		return true;
	}

	@Override
	protected ResultSet performRequest(SqlExecutor sqlExc) throws SQLException {
		//add to user table
		final String username = this.user;
		final String password = Utilities.Hashing.MD5Hash(this.pass);
		final String full_name = this.name;
		final String display_name = this.disp_name;
		final String e_mail = this.email;
		final String roomstr = "" + this.room;
		final String yearstr = "" + this.year;
		final String phonepre = this.phonePre;
		final String phonesuf = this.phoneSuf;
		sqlExc.executePreparedStatement("AddUser", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, username);
				ps.setString(2, password);
				ps.setString(3, full_name);
				ps.setString(4, display_name);
				ps.setString(5, e_mail);
				ps.setString(6, roomstr);
				ps.setString(7, yearstr);
				ps.setString(8, phonepre);
				ps.setString(9, phonesuf);
			}
		});
		//add user permissiongroup
		sqlExc.executePreparedStatement("AddUserPermissionGroup", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, username);
				ps.setString(2, Credentials.userPermission);
			}
		});
		return null;
	}
}
