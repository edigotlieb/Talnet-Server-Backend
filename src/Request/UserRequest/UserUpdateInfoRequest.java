/**
 * FILE : UserUpdateInfoRequest.java AUTHORS : Erez Gotlieb DESCRIPTION :
 */
package Request.UserRequest;

import Request.Credentials;
import Request.Exceptions.ValidationException;
import SQL.PreparedStatements.StatementPreparer;
import SQL.SqlExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserUpdateInfoRequest extends UserRequest {

	String userToChange, newName, newDispName, newEmail,newPhonePre,newPhoneSuf;
	int newYear, newRoom;

	public UserUpdateInfoRequest(String userToChange, String newName, String newDispName, String newEmail, int newYear, int newRoom, String newPhonePre, String newPhoneSuf, Credentials creds) {
		this(creds);
		this.userToChange = userToChange;
		this.newName = newName;
		this.newDispName = newDispName;
		this.newEmail = newEmail;
		this.newYear = newYear;
		this.newRoom = newRoom;
		this.newPhonePre = newPhonePre;
		this.newPhoneSuf = newPhoneSuf;
	}

	public UserUpdateInfoRequest(Credentials creds) {
		super(creds);
	}

	@Override
	public USER_ACTION_TYPE getActionType() {
		return USER_ACTION_TYPE.UPDATE_INFO;
	}

	@Override
	protected boolean CheckPermissions(SqlExecutor sqlExc) throws SQLException, ValidationException {
		if(this.userToChange.equals(Credentials.anonymous)){
			throw new ValidationException(28);
		}
		
		if (!this.creds.getUsername().equals(this.userToChange)) {
			throw new ValidationException(6);
		}
		return true;
	}

	@Override
	protected ResultSet performRequest(SqlExecutor sqlExc) throws SQLException {
		//add to user table
		final String username = this.userToChange;
		final String full_name = this.newName;
		final String display_name = this.newDispName;
		final String e_mail = this.newEmail;
		final String roomstr = "" + this.newRoom;
		final String yearstr = "" + this.newYear;
		final String phonePre = this.newPhonePre;
		final String phoneSuf = this.newPhoneSuf;
		sqlExc.executePreparedStatement("UpdateUser", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, full_name);
				ps.setString(2, display_name);
				ps.setString(3, e_mail);
				ps.setString(4, roomstr);
				ps.setString(5, yearstr);
				ps.setString(6, phonePre);
				ps.setString(7, phoneSuf);
				ps.setString(8, username);
			}
		});
		return null;
	}
}
