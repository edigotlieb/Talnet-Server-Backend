/** 
 * FILE : RequestException.java
 * AUTHORS : Erez Gotlieb    
 * DESCRIPTION : 
 */ 

package Request.Exceptions;

public abstract class RequestException extends Exception{

    int errorCode;
    
    public RequestException(int errorCode) {
        this.errorCode = errorCode;
    }
    
    public int getErrorCode() {
        return this.errorCode;
    }
    
    
}
