/** 
 * FILE : ValidationException.java
 * AUTHORS : Erez Gotlieb    
 * DESCRIPTION : 
 */ 

package Request.Exceptions;

public class ValidationException extends RequestException {

    public ValidationException(int errorCode) {
        super(errorCode);
    }

}
