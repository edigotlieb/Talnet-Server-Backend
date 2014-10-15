/**
 * FILE : RequestArgumentList.java AUTHORS : Idan Berkovits
 */
package Request.RequestArgument;

import Exceptions.ParsingException;
import Request.RequestArgument.ArgumentType.ArgumentType;
import org.w3c.dom.Element;

/**
 * implements a list request argument
 *
 * @author idanb55
 */
public class RequestArgumentList extends RequestArgument {

	private RequestArgumentStructure argumentStruct;

	/**
	 * constructs a list request argument
	 *
	 * @param eRequestArgumentList the element describes th request argument
	 * @throws ParsingException thrown in case of an xml parsing exception
	 */
	public RequestArgumentList(Element eRequestArgumentList) throws ParsingException {
		super(ArgumentType.ArgumentTypeFactory("List"), eRequestArgumentList.getAttribute("name"), eRequestArgumentList.getAttribute("key"));
		this.argumentStruct = new RequestArgumentStructure(eRequestArgumentList);
	}

	/**
	 * returns a structure of request arguments in the list argument
	 *
	 * @return a structure of request arguments in the list argument
	 */
	public RequestArgumentStructure getArgumentStruct() {
		return argumentStruct;
	}
}
