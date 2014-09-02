/** 
 * FILE : UserGetPermissionGroups.java
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

public class UserGetPermissionGroups extends UserRequest{

    public UserGetPermissionGroups(Credentials creds) {
        super(creds);
    }

    @Override
    public USER_ACTION_TYPE getActionType() {
        return USER_ACTION_TYPE.GET_GROUPS;
    }

    @Override
    protected boolean CheckPermissions(SqlExecutor sqlExc) throws SQLException, ValidationException {
        return true;
    }

    @Override
    protected ResultSet performRequest(SqlExecutor sqlExc) throws SQLException {
        final String username = this.creds.getUsername();
		return sqlExc.executePreparedStatement("getUserPermissionGroupsNoPass", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, username);
			}
		});
    }

}
