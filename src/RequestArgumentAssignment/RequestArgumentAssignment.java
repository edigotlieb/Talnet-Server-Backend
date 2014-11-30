/**
 * FILE : RequestArgumentAssignment.java AUTHORS : Idan Berkovits
 */
package RequestArgumentAssignment;

import Exceptions.ValidationException;
import Request.Credentials;
import Request.RequestArgument.ArgumentType.ArgumentType;
import Request.RequestArgument.RequestArgument;
import Request.RequestArgument.RequestArgumentList;
import Request.RequestArgument.RequestSingleArgument;
import org.json.JSONObject;

/**
 * this class represents an assignment of request argument
 *
 * @author idanb55
 */
public abstract class RequestArgumentAssignment {

	/**
	 * creates a request argument assignment
	 *
	 * @param argument the argument to assign
	 * @param requestData json object received from the clien
	 * @return returns the created RequestArgumentAssignment
	 * @throws ValidationException thrown in case of illegal value
	 */
	public static RequestArgumentAssignment RequestArgumentAssignmentFactory(RequestArgument argument, JSONObject requestData, Credentials creds) throws ValidationException {
		if (argument.getType().typeName().equals("List")) {
			return new RequestArgumentListAssignment((RequestArgumentList) argument, requestData.getJSONArray(argument.getKey()), creds);
		} else {
			return new RequestSingleArgumentAssignment((RequestSingleArgument) argument, requestData, creds);
		}
	}

	/**
	 * returns the argument name
	 *
	 * @return the argument name
	 */
	public abstract String getName();

	/**
	 * returns the argument type
	 *
	 * @return the argument type
	 */
	public abstract ArgumentType getType();

	/**
	 * returns the assigned value
	 *
	 * @return the assigned value
	 */
	public abstract Object getValue();
	
	@Override
	public String toString(){
		return this.getValue().toString();
	}
}
