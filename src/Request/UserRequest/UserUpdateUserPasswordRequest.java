/** 
 * FILE : UserUpdateUserPasswordRequest.java
 * AUTHORS : Erez Gotlieb    
 * DESCRIPTION : 
 */ 

package Request.UserRequest;

import Request.Credentials;
import Request.Exceptions.ValidationException;
import SQL.PreparedStatements.StatementPreparer;
import SQL.SqlExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserUpdateUserPasswordRequest extends UserRequest{

    String userToUpdate,newPass;
    
    public UserUpdateUserPasswordRequest(Credentials creds) {
        super(creds);
    }

    public UserUpdateUserPasswordRequest(Credentials creds, String userToUpdate,String newPass) {
        this(creds);
        this.userToUpdate = userToUpdate;
        this.newPass = newPass;
    }
    
    @Override
    public USER_ACTION_TYPE getActionType() {
        return USER_ACTION_TYPE.UPDATE_PASSWORD;
    }

    @Override
    protected boolean CheckPermissions(SqlExecutor sqlExc) throws SQLException, ValidationException {
        if (!this.creds.getUsername().equals(this.userToUpdate)) {
            throw new ValidationException(6);
        }
		
		if(!this.creds.isMasterApplication()){
			throw new ValidationException(13);
		}
        return true;
    }

    @Override
    protected ResultSet performRequest(SqlExecutor sqlExc) throws SQLException {
        final String username = this.userToUpdate;
		final String password = Utilities.Hashing.MD5Hash(this.newPass);
		sqlExc.executePreparedStatement("UpdateUserPassword", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, password);
				ps.setString(2, username);
			}
		});
		return null;
    }

}
