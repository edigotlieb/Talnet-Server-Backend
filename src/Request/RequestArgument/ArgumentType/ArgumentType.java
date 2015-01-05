/**
 * FILE : ArgumentType.java AUTHORS : Idan Berkovits
 */
package Request.RequestArgument.ArgumentType;

import Exceptions.ParsingException;

/**
 * implements a argument type
 *
 * @author idanb55
 */
public abstract class ArgumentType {

	/**
	 * creates the argument type given its string name
	 *
	 * @param type the argument type name
	 * @return the argument type instance
	 * @throws ParsingException thrown in case of unkown type
	 */
	public static ArgumentType ArgumentTypeFactory(String type) throws ParsingException {
		try {
			return SimpleType.value(SimpleType.SimpleTypeEnum.valueOf(type));
		} catch (IllegalArgumentException e) {
			try {
				return EnumType.enumMap.get(type);
			} catch (NullPointerException ex) {
				throw new ParsingException("Unkown request argument type " + type);
			}
		}
	}

	/**
	 * returns the type name
	 *
	 * @return the type name
	 */
	public abstract String typeName();

	/**
	 * validates a value
	 *
	 * @param value the value to validate
	 * @return true if a value is a legal value of the argument type
	 */
	public abstract boolean validateValue(String value);
}
