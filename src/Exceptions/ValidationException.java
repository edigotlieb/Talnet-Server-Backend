/**
 * FILE : ValidationException.java AUTHORS : Erez Gotlieb
 */
package Exceptions;

/**
 * a general validation exception
 *
 * @author idanb55
 */
public class ValidationException extends RequestException {

	/**
	 * constructs a ValidationException using an error code
	 *
	 * @param errorCode the exception error code
	 */
	public ValidationException(int errorCode) {
		super(errorCode);
	}
}
