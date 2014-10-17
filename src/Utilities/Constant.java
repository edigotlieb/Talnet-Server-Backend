/**
 * FILE : ErrorMsg.java AUTHORS : Idan Berkovits
 */
package Utilities;

import Exceptions.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * abstract class to handle the collection of error messages used in the request
 * processing
 *
 * @author idanb55
 */
public abstract class Constant {

	private static final Map<String, String> consts = new HashMap<>();

	/**
	 * adds error messages to the message collection
	 *
	 * @param errorMsgs an xml nodeList of error messages
	 * @throws ParsingException in case of xml parsing exception
	 */
	public static void addConstants(NodeList constants) throws ParsingException {
		for (int i = 0; i < constants.getLength(); i++) {
			Element constant = (Element) constants.item(i);
			consts.put(constant.getAttribute("name"), constant.getTextContent().toUpperCase());
		}
	}

	/**
	 * retrieves an error message using an error code
	 *
	 * @param errorCode the error code
	 * @return the retrieved message
	 */
	public static String getValue(String name) {
		return consts.get(name.toUpperCase());
	}
	
	public static Set<String> getNames(){
		return consts.keySet();
	}
}
