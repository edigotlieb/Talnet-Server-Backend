/**
 * FILE : ArgumentSet.java AUTHORS : Idan Berkovits
 */
package Request.Argument;

import Exceptions.ParsingException;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * implements a set of arguments
 *
 * @author idanb55
 */
public class ArgumentSet {

	private Map<Integer, Argument> arguments;

	/**
	 * adds an argument to the argument collection
	 *
	 * @param argument
	 */
	public final void addArgument(Argument argument) {
		this.arguments.put(argument.getId(), argument);
	}

	/**
	 * constructs a new ArgumentSet using an xml element
	 *
	 * @param eArguments the xml element describes the argument set
	 * @throws ParsingException thrown in case of xml parsing exception
	 */
	public ArgumentSet(Element eArguments) throws ParsingException {
		arguments = new HashMap<>();
		NodeList nlArguments = eArguments.getElementsByTagName("argument");
		for (int i = 0; i < nlArguments.getLength(); i++) {
			this.addArgument(new Argument((Element) nlArguments.item(i)));
		}
	}

	/**
	 * retrieves an argument using the argument id
	 *
	 * @param id the argument id
	 * @return the retrieved argument
	 */
	public Argument get(int id) {
		return arguments.get(id);
	}

	/**
	 * returns the size of the set
	 * @return the size of the set
	 */
	public int getSize() {
		return arguments.size();
	}
}
