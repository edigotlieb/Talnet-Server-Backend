/**
 * FILE : RequestArgumentListAssignment.java AUTHORS : Idan Berkovits
 */
package RequestArgumentAssignment;

import Exceptions.ValidationException;
import Request.Credentials;
import Request.RequestArgument.ArgumentType.ArgumentType;
import Request.RequestArgument.RequestArgumentList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * this class represents an assignment of request argument list
 *
 * @author idanb55
 */
public class RequestArgumentListAssignment extends RequestArgumentAssignment {

	private List<RequestArgumentStructureAssignment> argumentList;
	private RequestArgumentList arguments;

	/**
	 * constructs a request argument list assignment
	 * @param arguments the request argument list to assign
	 * @param requestData the data received from the client to assign with
	 * @throws ValidationException thrown in case of illegal value
	 */
	public RequestArgumentListAssignment(RequestArgumentList arguments, JSONArray requestData, Credentials creds) throws ValidationException {
		this.arguments = arguments;
		this.argumentList = new LinkedList<>();
		for (int i = 0; i < requestData.length(); i++) {
			JSONObject requestSubData = requestData.getJSONObject(i);
			this.argumentList.add(new RequestArgumentStructureAssignment(arguments.getArgumentStruct(), requestSubData, creds));
		}
	}

	@Override
	public String getName() {
		return this.arguments.getName();
	}

	@Override
	public ArgumentType getType() {
		return this.arguments.getType();
	}

	@Override
	public Object getValue() {
		return argumentList;
	}

	/**
	 * returns an iterator over the request argument structure assignment
	 * @return an iterator over the request argument structure assignment
	 */
	public Iterator<RequestArgumentStructureAssignment> iterator() {
		return this.argumentList.iterator();
	}
}
