/**
 * FILE : SimpleType.java AUTHORS : Idan Berkovits
 */
package Request.RequestArgument.ArgumentType;

import java.util.HashMap;
import java.util.Map;

/**
 * implements a enumType described in an xml file
 *
 * @author idanb55
 */
public class SimpleType extends ArgumentType {

	private final static Map<SimpleType.SimpleTypeEnum, SimpleType> instances;

	static {
		instances = new HashMap<>();
		for (SimpleType.SimpleTypeEnum type : SimpleType.SimpleTypeEnum.values()) {
			instances.put(type, new SimpleType(type));
		}
	}

	/**
	 * returns an instance of a simple type given its name
	 * @param type the name of the simple type
	 * @return the simple type instance
	 */
	public static SimpleType value(SimpleType.SimpleTypeEnum type) {
		return instances.get(type);
	}

	@Override
	public String typeName() {
		return this.type.toString();
	}

	protected enum SimpleTypeEnum {

		String, Integer, Boolean, Statement, List, Map
	}
	private SimpleTypeEnum type;

	protected SimpleType(SimpleTypeEnum type) {
		this.type = type;
	}

	@Override
	public boolean validateValue(String value) {
		switch (this.type) {
			case String:
			case List:
			case Statement:
				return true;
			case Integer:
				try {
					Integer.parseInt(value);
					return true;
				} catch (NumberFormatException e) {
					return false;
				}
			case Boolean:
				return value.toLowerCase().matches("true|false");
			default:
				return false;
		}
	}
}
