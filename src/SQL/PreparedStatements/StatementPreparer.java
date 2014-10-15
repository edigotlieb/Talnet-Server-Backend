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

public abstract class StatementPreparer {

    public abstract void prepareStatement(PreparedStatement ps) throws SQLException;
	
	protected void prepareStatement(PreparedStatement ps, ArgumentSet arguments, RequestArgumentStructureAssignment requestArguments, Credentials creds) throws SQLException{
		for(int i = 0; i < arguments.getSize(); i++){
			Argument argument = arguments.get(i);
			ps.setString(argument.getId(), argument.getValue(requestArguments, creds));
		}
	}
}
