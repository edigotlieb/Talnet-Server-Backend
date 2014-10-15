/**
 * FILE : RequestArgumentStructure.java AUTHORS : Idan Berkovits
 */
package Request.RequestArgument;

import Exceptions.ParsingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * implements a request argument structure
 *
 * @author idanb55
 */
public class RequestArgumentStructure {

	private Map<String, RequestArgument> arguments;

	/**
	 * adds a request argument to the structure
	 *
	 * @param requestArgument the argument to add to the structure
	 */
	public final void addArgument(RequestArgument requestArgument) {
		this.arguments.put(requestArgument.getName(), requestArgument);
	}

	/**
	 * constructs a new structure of request arguments using an xml element
	 *
	 * @param eRequestArguments the xml element describing the request arguments
	 * @throws ParsingException
	 */
	public RequestArgumentStructure(Element eRequestArguments) throws ParsingException {
		arguments = new HashMap<>();
		//add the single arguments
		NodeList requestArguments = eRequestArguments.getElementsByTagName("requestArgument");
		for (int i = 0; i < requestArguments.getLength(); i++) {
			if (!eRequestArguments.isSameNode(requestArguments.item(i).getParentNode())) {
				continue;
			}
			this.addArgument(RequestArgument.requestArgumentFactory((Element) requestArguments.item(i)));
		}

		//add the list arguments
		requestArguments = eRequestArguments.getElementsByTagName("requestArgumentList");
		for (int i = 0; i < requestArguments.getLength(); i++) {
			if (!eRequestArguments.isSameNode(requestArguments.item(i).getParentNode())) {
				continue;
			}
			this.addArgument(new RequestArgumentList((Element) requestArguments.item(i)));
		}
	}

	/**
	 * returns the size of the structure
	 *
	 * @return the size of the structure
	 */
	public int size() {
		return this.arguments.size();
	}

	/**
	 * returns a argument from the structure by its name
	 *
	 * @param name the name of the argument to return
	 * @return the retrieved argument
	 */
	public RequestArgument getArgument(String name) {
		return this.arguments.get(name);
	}

	/**
	 * returns a collection of arguments in the structure
	 *
	 * @return a collection of arguments in the structure
	 */
	public Collection<RequestArgument> getArguments() {
		return this.arguments.values();
	}
}
