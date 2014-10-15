/**
 * FILE : RequestArgumentStructureAssignment.java AUTHORS : Idan Berkovits
 */
package RequestArgumentAssignment;

import Exceptions.ValidationException;
import Request.RequestArgument.RequestArgumentStructure;
import Request.RequestArgument.RequestArgument;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 * this class represents an assignment of request argument structure
 *
 * @author idanb55
 */
public final class RequestArgumentStructureAssignment {

	private Map<String, RequestArgumentAssignment> arguments;

	/**
	 * adds a new argument to the structure
	 *
	 * @param requestArgument the argument to add
	 */
	public final void addArgument(RequestArgumentAssignment requestArgument) {
		this.arguments.put(requestArgument.getName(), requestArgument);
	}

	/**
	 * constructs a request argument structure assignment
	 *
	 * @param arguments the argument structure to assign
	 * @param requestData the data received fro the client to assign with
	 * @throws ValidationException thrown in case of illegal value
	 */
	public RequestArgumentStructureAssignment(RequestArgumentStructure arguments, JSONObject requestData) throws ValidationException {
		this.arguments = new HashMap<>();
		Iterator<RequestArgument> i = arguments.getArguments().iterator();
		while (i.hasNext()) {
			this.addArgument(RequestArgumentAssignment.RequestArgumentAssignmentFactory(i.next(), requestData));
		}
		Logger.getGlobal().log(Level.FINE,this.toString());
	}

	private RequestArgumentStructureAssignment() {
	}

	/**
	 * returns the argument collection
	 *
	 * @return the argument collection
	 */
	public Collection<RequestArgumentAssignment> getArguments() {
		return arguments.values();
	}

	/**
	 * returns an argument by name
	 *
	 * @param name the argument name (could be in the format: {nameArgument})
	 * @return the request argument assignment
	 */
	public RequestArgumentAssignment getArgument(String name) {
		if (name.matches("\\{[\\w]*\\}")) {
			name = name.substring(1, name.length() - 1);
		}
		return this.arguments.get(name);
	}

	/**
	 * merges two request argument structure assignments
	 * @param other the request argument structure assignment to merge
	 * @return the merged structure
	 */
	public RequestArgumentStructureAssignment merge(RequestArgumentStructureAssignment other) {
		RequestArgumentStructureAssignment result = (RequestArgumentStructureAssignment) this.clone();
		result.arguments.putAll(other.arguments);
		return result;
	}

	@Override
	protected RequestArgumentStructureAssignment clone() {
		RequestArgumentStructureAssignment result = new RequestArgumentStructureAssignment();
		result.arguments = new HashMap<>(arguments);
		return result;
	}
	
	@Override
	public String toString(){
		return this.arguments.toString();
	}
}
