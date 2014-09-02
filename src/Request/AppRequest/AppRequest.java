/** 
 * FILE : AppRequest.java
 * AUTHORS : Erez Gotlieb    
 * DESCRIPTION : 
 */ 

package Request.AppRequest;

import Request.Credentials;
import Request.Request;

public abstract class AppRequest extends Request{

    public AppRequest(Credentials creds) {
        super(creds);
    }
    
    public enum APP_ACTION_TYPE {
        ADD_TABLE,DROP_TABLE,DELETE_APP,ADD_PERMISSIONGROUP,REMOVE_PERMISSIONGROUP,GET_TABLE_PERMISSIONS,
        CREATE_APP,GET_TABLE_INFO,GET_TABLES,SET_PERMISSIONGROUP_ADMIN,ADD_PERMISSION_GROUP_FOR_TABLE,REMOVE_PERMISSION_GROUP_FOR_TABLE, GET_ALL_APPS,GET_USER_APPS,GET_ALL_PERMISSIONS;
    }
    
    @Override
    public final TYPE getType() {
        return Request.TYPE.APP;
    }
    
    
    public abstract APP_ACTION_TYPE getActionType();

}
