/**
 * FILE : EnumType.java AUTHORS : Idan Berkovits
 */
package Request.RequestArgument.ArgumentType;

import Exceptions.ParsingException;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * implements a enumType described in an xml file
 *
 * @author idanb55
 */
public class EnumType extends ArgumentType {

	protected static Map<String, EnumType> enumMap;

	/**
	 * adds enum types to the collection using a node list of enum types
	 *
	 * @param enumTypes the node list of enum types
	 * @throws ParsingException thrown in case of a parsing exception
	 */
	public static void addEnumTypes(NodeList enumTypes) throws ParsingException {
		if (enumMap == null) {
			enumMap = new HashMap<>();
		}
		for (int i = 0; i < enumTypes.getLength(); i++) {
			EnumType enumType = new EnumType((Element) enumTypes.item(i));
			enumMap.put(enumType.typeName(), enumType);
		}
	}
	private String enumName;
	private String[] values;

	/**
	 * constructs an enum type given its xml element
	 * @param eEnumType the xml element
	 */
	public EnumType(Element eEnumType) {
		this.enumName = eEnumType.getAttribute("name");
		NodeList valueList = eEnumType.getElementsByTagName("value");
		this.values = new String[valueList.getLength()];
		for (int i = 0; i < values.length; i++) {
			values[i] = valueList.item(i).getTextContent();
		}
	}

	@Override
	public String typeName() {
		return this.enumName;
	}

	@Override
	public boolean validateValue(String value) {
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(value)) {
				return true;
			}
		}
		return false;
	}
}
