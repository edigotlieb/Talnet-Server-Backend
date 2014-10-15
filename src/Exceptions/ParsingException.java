/**
 * FILE : ParsingException.java AUTHORS : Idan Berkovits
 */
package Exceptions;

/**
 * a general parsing exception
 *
 * @author idanb55
 */
public class ParsingException extends Exception {

	private String fileName;

	/**
	 * constructs a parsing exception with a given message
	 *
	 * @param message the exception message
	 */
	public ParsingException(String message) {
		super(message);
	}

	/**
	 * constructs a parsing exception with a given message and a cause
	 *
	 * @param message the exception message
	 * @param cause the exception cause
	 */
	public ParsingException(String message, Throwable cause) {
		super(message, cause);
	}
}
