/**
 * FILE : StringFunction.java AUTHORS : Idan Berkovits
 */
package Request.Argument;

import Exceptions.ParsingException;
import Utilities.Hashing;
import java.util.HashMap;
import java.util.Map;

/**
 * implements a string function (string to string)
 *
 * @author idanb55
 */
public abstract class StringFunction {

	/**
	 * this string function does nothing
	 */
	public final static StringFunction EMPTY_FUNCTION;
	private final static Map<String, StringFunction> stringFunctions;

	static {
		stringFunctions = new HashMap<>();
		// md5 function
		stringFunctions.put("md5", new StringFunction() {
			@Override
			public String function(String target) {
				return Hashing.MD5Hash(target);
			}
		});
		// empty function
		stringFunctions.put("", new StringFunction() {
			@Override
			public String function(String target) {
				return target;
			}
		});
		EMPTY_FUNCTION = stringFunctions.get("");
	}

	/**
	 * the function
	 *
	 * @param target the string to perform the function of
	 * @return the result of the function
	 */
	public abstract String function(String target);

	/**
	 * returns a StringFunction using a function name
	 *
	 * @param function the requested function name
	 * @return the StringFunction instance
	 * @throws ParsingException thrown in case of an illegal function name
	 */
	public static StringFunction get(String function) throws ParsingException {
		try {
			return stringFunctions.get(function);
		} catch (NullPointerException e) {
			throw new ParsingException("Unkown String Function " + function);
		}
	}
}
