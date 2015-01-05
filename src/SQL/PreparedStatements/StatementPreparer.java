/**
 * FILE : StatementPreparer.java AUTHORS : Erez Gotlieb
 */
package SQL.PreparedStatements;

import Request.Argument.Argument;
import Request.Argument.ArgumentSet;
import Request.Credentials;
import RequestArgumentAssignment.RequestArgumentStructureAssignment;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

public abstract class StatementPreparer {

	public abstract void prepareStatement(PreparedStatement ps) throws SQLException;

	protected void prepareStatement(PreparedStatement ps, ArgumentSet arguments, RequestArgumentStructureAssignment requestArguments, Credentials creds) throws SQLException {
		int parametersLeft = ps.getParameterMetaData().getParameterCount();
		Iterator<Integer> it = arguments.keySet().iterator();
		while (it.hasNext() && parametersLeft > 0) {
			Argument argument = arguments.get(it.next());
			switch(argument.getType()){
				case Integer:
					ps.setInt(argument.getId(), Integer.parseInt(argument.getValue(requestArguments, creds)));
					break;
				case String:
				default:
					ps.setString(argument.getId(), argument.getValue(requestArguments, creds));
			}
			parametersLeft--;
		}
	}
}
