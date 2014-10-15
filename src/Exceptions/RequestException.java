/**
 * FILE : RequestException.java AUTHORS : Erez Gotlieb
 */
package Exceptions;

/**
 * a general request exception
 *
 * @author idanb55
 */
public class RequestException extends Exception {

	int errorCode;

	/**
	 * constructs an exception with a cause
	 *
	 * @param cause the exception cause
	 */
	public RequestException(Throwable cause) {
		super(cause);
		this.errorCode = 0;
	}

	/**
	 * constructs an exception with an error code
	 *
	 * @param errorCode the exception error code
	 */
	public RequestException(int errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * retrieves the exception error code
	 * @return the exception error code
	 */
	public int getErrorCode() {
		return this.errorCode;
	}

	@Override
	public String getMessage() {
		if (this.errorCode > 0) {
			return ErrorMsg.getErrorMsg(this.errorCode);
		}
		return super.getMessage();
	}
}
