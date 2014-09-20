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

	String userToChange, newFirstName, newLastName, newDispName, newEmail,newPhonePre,newPhoneSuf;
	int newYear, newRoom;

	public UserUpdateInfoRequest(String userToChange, String newFirstName, String newLastName, String newDispName, String newEmail, int newYear, int newRoom, String newPhonePre, String newPhoneSuf, Credentials creds) {
		this(creds);
		this.userToChange = userToChange;
		this.newFirstName = newFirstName;
		this.newLastName = newLastName;
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
		final String firstname = this.newFirstName;
		final String lastname = this.newLastName;
		final String display_name = this.newDispName;
		final String e_mail = this.newEmail;
		final String roomstr = "" + this.newRoom;
		final String yearstr = "" + this.newYear;
		final String phonePre = this.newPhonePre;
		final String phoneSuf = this.newPhoneSuf;
		sqlExc.executePreparedStatement("UpdateUser", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, firstname);
				ps.setString(2, lastname);
				ps.setString(3, display_name);
				ps.setString(4, e_mail);
				ps.setString(5, roomstr);
				ps.setString(6, yearstr);
				ps.setString(7, phonePre);
				ps.setString(8, phoneSuf);
				ps.setString(9, username);
			}
		});
		return null;
	}
}
