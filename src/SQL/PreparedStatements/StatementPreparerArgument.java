/**
 * FILE : StatementPreparerArgument.java AUTHORS : Idan Berkovits
 */
package SQL.PreparedStatements;

import Request.Argument.ArgumentSet;
import Request.Credentials;
import RequestArgumentAssignment.RequestArgumentStructureAssignment;
import Statement.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * a statement prepare that use request argument assignment
 *
 * @author idanb55
 */
public class StatementPreparerArgument extends StatementPreparer {

	private ArgumentSet arguments;
	private RequestArgumentStructureAssignment requestArguments;
	private Credentials creds;

	/**
	 * constructs a statement preparer with a set of arguments and a argument
	 * assignment
	 *
	 * @param arguments the argument set to use
	 * @param requestArguments the request argument assignment
	 * @param creds the credentials to use
	 */
	public StatementPreparerArgument(ArgumentSet arguments, RequestArgumentStructureAssignment requestArguments, Credentials creds) {
		this.arguments = arguments;
		this.requestArguments = requestArguments;
		this.creds = creds;
	}

	/**
	 * returns an argument value using its id
	 *
	 * @param id
	 * @return
	 */
	public String getArgumentValue(int id) {
		return this.arguments.get(id).getValue(requestArguments, creds);
	}

	/**
	 * returns a value of an argument when it is a statement
	 *
	 * @param id the id of the argument
	 * @return a Statement instance created using the argument assignment
	 */
	public Statement getArgumentStatement(int id) {
		return (Statement) requestArguments.getArgument(this.arguments.get(id).getValue()).getValue();
	}
	
	
	/**
	 * returns a value of an argument when it is a map
	 *
	 * @param id the id of the argument
	 * @return a Map instance created using the argument assignment
	 */
	public Map<String, String> getArgumentMap(int id) {
		return (Map<String, String>) requestArguments.getArgument(this.arguments.get(id).getValue()).getValue();
	}

	/**
	 * returns a list of structure argument (to use when an argument is a list
	 * type)
	 *
	 * @param id the id of the list argument
	 * @return a list of structure argument
	 */
	public List<RequestArgumentStructureAssignment> getArgumentList(int id) {
		return (List<RequestArgumentStructureAssignment>) requestArguments.getArgument(this.arguments.get(id).getValue()).getValue();
	}

	@Override
	public void prepareStatement(PreparedStatement ps) throws SQLException {
		this.prepareStatement(ps, arguments, requestArguments, creds);
	}
}
