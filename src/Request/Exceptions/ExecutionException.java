/** 
 * FILE : ExecutionException.java
 * AUTHORS : Erez Gotlieb    
 * DESCRIPTION : 
 */ 

package Request.Exceptions;

public class ExecutionException extends RequestException {

    public ExecutionException(int errorCode) {
        super(errorCode);
    }

}
