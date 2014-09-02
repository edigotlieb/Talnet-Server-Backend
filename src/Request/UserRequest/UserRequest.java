/** 
 * FILE : UserRequest.java
 * AUTHORS : Erez Gotlieb    
 * DESCRIPTION : 
 */ 

package Request.UserRequest;

import Request.Credentials;
import Request.Request;

public abstract class UserRequest extends Request{

    public UserRequest(Credentials creds) {
        super(creds);
    }
    
    public enum USER_ACTION_TYPE {
        SIGN_UP,SIGN_IN,UPDATE_INFO,ADD_PERMISSION,SELECT,UPDATE_PASSWORD,REMOVE_PERMISSION,GET_GROUPS,GET_USERS_WITH_GROUPS, DELETE_USER
    }
    
    @Override
    public final TYPE getType() {
        return Request.TYPE.USER;
    }
    
    abstract public USER_ACTION_TYPE getActionType();
}
