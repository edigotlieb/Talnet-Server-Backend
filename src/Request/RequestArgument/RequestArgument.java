/**
 * FILE : RequestArgument.java AUTHORS : Idan Berkovits
 */
package Request.RequestArgument;

import Exceptions.ParsingException;
import Request.RequestArgument.ArgumentType.ArgumentType;
import org.w3c.dom.Element;

/**
 * implements a request argument
 *
 * @author idanb55
 */
public abstract class RequestArgument {

	/**
	 * creates a new request argument (single or list)
	 *
	 * @param eRequestArgument the element describes the request argument
	 * @return the created request argument
	 */
	public static RequestArgument requestArgumentFactory(Element eRequestArgument) throws ParsingException {
		switch (eRequestArgument.getNodeName()) {
			case "requestArgument":
				return new RequestSingleArgument(eRequestArgument);
			case "requestArgumentList":
				return new RequestArgumentList(eRequestArgument);
			default:
				throw new ParsingException("Unkown requestArgument " + eRequestArgument.getNodeName());
		}
	}
	private ArgumentType type;
	private String name;
	private String key;

	/**
	 * returns the argument type
	 *
	 * @return the argument type
	 */
	public ArgumentType getType() {
		return type;
	}

	/**
	 * returns the argument name
	 *
	 * @return the argument name
	 */
	public String getName() {
		return name;
	}

	/**
	 * returns the argument key
	 *
	 * @return the argument key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * constructs a general request argument
	 *
	 * @param type the argument type
	 * @param name the argument name
	 * @param key the argument key
	 */
	public RequestArgument(ArgumentType type, String name, String key) {
		this.type = type;
		this.name = name;
		this.key = key;
	}
}
