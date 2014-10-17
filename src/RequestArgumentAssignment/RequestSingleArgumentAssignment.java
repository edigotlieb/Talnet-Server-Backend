/**
 * FILE : RequestSingleArgumentAssignment.java AUTHORS : Idan Berkovits
 */
package RequestArgumentAssignment;

import Exceptions.ValidationException;
import Request.RequestArgument.ArgumentType.ArgumentType;
import Request.RequestArgument.RequestArgument;
import Request.RequestArgument.RequestSingleArgument;
import Statement.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 * this class represents an assignment of a single request argument
 *
 * @author idanb55
 */
public class RequestSingleArgumentAssignment extends RequestArgumentAssignment {

	private RequestArgument argument;
	private Object value;

	/**
	 * constructs a new single request argument assignment
	 *
	 * @param argument the argument to assign
	 * @param requestData the data received from the client
	 * @throws ValidationException thrown in case of illegal value
	 */
	public RequestSingleArgumentAssignment(RequestSingleArgument argument, JSONObject requestData) throws ValidationException {
		this.argument = argument;
		switch (argument.getType().typeName()) {
			case "Statement":
				this.value = Statement.statementFactory(requestData.getJSONObject(argument.getKey()));
				break;
			case "Map":
				this.value = assignMap(new HashMap<String, String>(), requestData.getJSONObject(argument.getKey()));
				break;
			default:
				String strValue;
				if (argument.getDefValue() != null && !requestData.has(argument.getKey())) {
					strValue = argument.getDefValue();
				} else {
					strValue = requestData.get(argument.getKey()).toString();
				}
				if (!argument.getType().validateValue(strValue)) {
					//validation of the assigned value
					Logger.getGlobal().log(Level.FINE, "value validation failed...",
							new IllegalArgumentException("name: " + argument.getName() + ", type: " + argument.getType().typeName() + ", value: " + strValue));
					throw new ValidationException(100);
				}
				this.value = strValue;
		}
	}

	/**
	 * returns the request argument
	 *
	 * @return the request argument
	 */
	public RequestArgument getArgument() {
		return argument;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public String getName() {
		return this.getArgument().getName();
	}

	@Override
	public ArgumentType getType() {
		return this.getArgument().getType();
	}

	/**
	 * assigns values into a map key=>value from a json object
	 *
	 * @param map the map to add the values into
	 * @param requestData the json object to retrieve the value from
	 * @return a reference to the same map given in the parameter
	 */
	private static Map<String, String> assignMap(Map<String, String> map, JSONObject requestData) {
		Iterator it = requestData.keys();
		while (it.hasNext()) {
			map.put((String) it.next(), requestData.get((String) it.next()).toString());
		}
		return map;
	}
}
