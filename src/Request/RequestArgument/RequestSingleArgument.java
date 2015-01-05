/**
 * FILE : RequestSingleArgument.java AUTHORS : Idan Berkovits
 */
package Request.RequestArgument;

import Exceptions.ParsingException;
import Request.RequestArgument.ArgumentType.ArgumentType;
import org.w3c.dom.Element;

/**
 * implements a single request argument
 *
 * @author idanb55
 */
public class RequestSingleArgument extends RequestArgument {

	private String defValue;

	/**
	 * constructs a new single request argument
	 *
	 * @param eRequestArgument the element describing the request argument
	 * @throws ParsingException thrown in case of an xml parsing exception
	 */
	protected RequestSingleArgument(Element eRequestArgument) throws ParsingException {
		super(ArgumentType.ArgumentTypeFactory(eRequestArgument.getAttribute("type")), eRequestArgument.getAttribute("name"), eRequestArgument.getAttribute("key"));
		if (eRequestArgument.hasAttribute("defvalue")) {
			this.defValue = eRequestArgument.getAttribute("defvalue");
		} else {
			this.defValue = null;
		}
	}

	/**
	 * returns the default value of the argument
	 *
	 * @return the default value of the argument
	 */
	public String getDefValue() {
		return defValue;
	}
}
