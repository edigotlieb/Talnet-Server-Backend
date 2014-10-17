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
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class StatementPreparer {

	public abstract void prepareStatement(PreparedStatement ps) throws SQLException;

	protected void prepareStatement(PreparedStatement ps, ArgumentSet arguments, RequestArgumentStructureAssignment requestArguments, Credentials creds) throws SQLException {
		Iterator<Integer> it = arguments.keySet().iterator();
		while (it.hasNext()) {
			Argument argument = arguments.get(it.next());
			Logger.getGlobal().log(Level.FINE, "DEBUG ps argument id {0} value {1} real value {2}", new Object[]{argument.getId(), argument.getValue(), argument.getValue(requestArguments, creds)});
			ps.setString(argument.getId(), argument.getValue(requestArguments, creds));
		}
	}
}
