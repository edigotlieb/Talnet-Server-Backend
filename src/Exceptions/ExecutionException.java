/**
 * FILE : ExecutionException.java AUTHORS : Erez Gotlieb
 */
package Exceptions;

import java.sql.SQLException;

/**
 * a general execution exception
 *
 * @author idanb55
 */
public class ExecutionException extends RequestException {

	/**
	 * constructs an ExecutionException using an error code
	 *
	 * @param errorCode the exception error code
	 */
	public ExecutionException(int errorCode) {
		super(errorCode);
	}

	/**
	 * constructs an ExecutionException using a SQL exception as a cause
	 *
	 * @param ex the cause SQL exception
	 */
	public ExecutionException(SQLException ex) {
		super(ex);
	}
}
