/**
 * FILE : ErrorMsg.java AUTHORS : Erez Gotlieb
 */
package Exceptions;

import java.util.HashMap;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * abstract class to handle the collection of error messages used in the request
 * processing
 *
 * @author idanb55
 */
public abstract class ErrorMsg {

	private static final HashMap<Integer, String> msgs;

	static {
		msgs = new HashMap<>();

		//execution exceptions
		msgs.put(51, "Must validate before executing");
		msgs.put(52, "General excution error");

		// 
		msgs.put(100, "Bad request format");
		msgs.put(500, "Internal Server Error!");
		msgs.put(501, "Request to long!");
	}

	/**
	 * adds error messages to the message collection
	 *
	 * @param errorMsgs an xml nodeList of error messages
	 * @throws ParsingException in case of xml parsing exception
	 */
	public static void addErrorMsg(NodeList errorMsgs) throws ParsingException {
		for (int i = 0; i < errorMsgs.getLength(); i++) {
			Element errorMsg = (Element) errorMsgs.item(i);
			msgs.put(Integer.parseInt(errorMsg.getAttribute("id")), errorMsg.getTextContent());
		}
	}

	/**
	 * retrieves an error message using an error code
	 *
	 * @param errorCode the error code
	 * @return the retrieved message
	 */
	public static String getErrorMsg(int errorCode) {
		return msgs.get(errorCode);
	}
}
