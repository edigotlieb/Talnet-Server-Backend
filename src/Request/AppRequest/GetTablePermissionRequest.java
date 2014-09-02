/** 
 * FILE : GetTablePermissionRequest.java
 * AUTHORS : Erez Gotlieb    
 * DESCRIPTION : 
 */ 

package Request.AppRequest;

import Request.Credentials;
import Request.Exceptions.ValidationException;
import SQL.PreparedStatements.StatementPreparer;
import SQL.SqlExecutor;
import SQL.Utilities.ExistenceValidator;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetTablePermissionRequest extends AppRequest{

    String appName;
    String tableName;

    public GetTablePermissionRequest(String appName, String tableName, Credentials creds) {
        super(creds);
        this.appName = appName;
        this.tableName = this.appName+"_"+tableName;
    }        
    
    @Override
    public APP_ACTION_TYPE getActionType() {
        return AppRequest.APP_ACTION_TYPE.GET_TABLE_PERMISSIONS;
    }

    @Override
    protected ResultSet performRequest(SqlExecutor sqlExc) throws SQLException {
        final String tableName = this.tableName;
        return sqlExc.executePreparedStatement("getTablePermissions", new StatementPreparer() {
            @Override           
            public void prepareStatement(PreparedStatement ps) throws SQLException {                
                ps.setString(1, tableName);
            }
        });        
    }

    @Override
    protected boolean CheckPermissions(SqlExecutor sqlExc) throws SQLException, ValidationException {
        	if (!ExistenceValidator.isAppByName(sqlExc, this.appName)) {
			throw new ValidationException(1);
		}
		
		if (!this.creds.isAppSuperAdmin(this.appName)) {
			throw new ValidationException(6);
		}
		
		if (!ExistenceValidator.isTableByName(sqlExc,this.tableName)) {
			throw new ValidationException(8);
		}

		return true;
    }

}
