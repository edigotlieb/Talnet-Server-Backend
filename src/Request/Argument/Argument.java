/**
 * FILE : Argument.java AUTHORS : Idan Berkovits
 */
package Request.Argument;

import Exceptions.ParsingException;
import Request.Credentials;
import RequestArgumentAssignment.RequestArgumentStructureAssignment;
import RequestArgumentAssignment.RequestArgumentAssignment;
import Utilities.BackendParams;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;

/**
 * implements an argument of a validation or a performance type
 *
 * @author idanb55
 */
public class Argument {

	private int id;
	private String value;
	private StringFunction function;

	/**
	 * constructs a new argument using an xml element
	 *
	 * @param eArgument the argument element
	 * @throws ParsingException thrown in case of an xml parsing exception
	 */
	public Argument(Element eArgument) throws ParsingException {
		this.id = Integer.parseInt(eArgument.getAttribute("id"));
		this.value = eArgument.getTextContent();
		if (eArgument.hasAttribute("function")) {
			this.function = StringFunction.get(eArgument.getAttribute("function"));
		} else {
			this.function = StringFunction.get("");
		}
	}

	/**
	 * returns the id of the argument
	 *
	 * @return the id of the argument
	 */
	public int getId() {
		return id;
	}

	/**
	 * returns the value of the argument (retrieved from the xml file)
	 *
	 * @return the value of the argument
	 */
	public String getValue() {
		return value;
	}

	/**
	 * returns the value computed with argument assignment and credentials
	 * instance
	 *
	 * @param requestArguments
	 * @param creds
	 * @return the computed value
	 */
	public String getValue(RequestArgumentStructureAssignment requestArguments, Credentials creds) {
		return Argument.getValue(value, function, requestArguments, creds);
	}

	/**
	 * returns the value computed with argument assignment and credentials
	 * instance from src string
	 *
	 * @param src
	 * @param requestArguments
	 * @param creds
	 * @return the computed value
	 */
	public static String getValue(String src, RequestArgumentStructureAssignment requestArguments, Credentials creds) {
		return Argument.getValue(src, StringFunction.EMPTY_FUNCTION, requestArguments, creds);
	}

	/**
	 * returns the value computed with argument assignment and credentials
	 * instance from src string and using a given string function
	 *
	 * @param src
	 * @param function
	 * @param requestArguments
	 * @param creds
	 * @return the computed value
	 */
	private static String getValue(String src, StringFunction function, RequestArgumentStructureAssignment requestArguments, Credentials creds) {
		Iterator<RequestArgumentAssignment> i = requestArguments.getArguments().iterator();
		while (i.hasNext()) {
			RequestArgumentAssignment argument = i.next();
			switch (argument.getType().typeName()) {
				case "List":
				case "Statement":
				case "Map":
					continue;
				default:
					src = src.replace("{" + argument.getName() + "}", (String) argument.getValue());
			}
		}
		src = src.replace("{creds:username}", creds.getUsername());
		src = src.replace("{creds:appname}", creds.getAppName());
		Iterator<String> backendParams = BackendParams.getNames().iterator();
		while (backendParams.hasNext()) {
			String paramName = backendParams.next();
			Logger.getGlobal().log(Level.FINE, "DEBUG {0}", paramName);
			Logger.getGlobal().log(Level.FINE, "DEBUG {0}", src);
			src = src.replace("{BackendParams:" + paramName + "}", BackendParams.getParameter(paramName));
			Logger.getGlobal().log(Level.FINE, "DEBUG {0}", src);
		}

		return function.function(src);
	}

	/**
	 * returns the argument string function
	 *
	 * @return the argument string function
	 */
	public StringFunction getFunction() {
		return function;
	}
}
