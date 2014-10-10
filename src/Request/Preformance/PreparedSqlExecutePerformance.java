/**
 * FILE : PreparedSqlExecutePerformance.java AUTHORS : Idan Berkovits
 */
package Request.Preformance;

import Exceptions.ParsingException;
import Request.Credentials;
import RequestArgumentAssignment.RequestArgumentStructureAssignment;
import SQL.PreparedStatements.StatementPreparerArgument;
import SQL.SqlExecutor;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.w3c.dom.Element;

/**
 * a prepared sql execution performance type
 *
 * @author idanb55
 */
public class PreparedSqlExecutePerformance extends Performance {

	/**
	 * constructs a prepared sql execution performance using an xml element
	 *
	 * @param ePerformance the performance xml element
	 * @throws ParsingException thrown in case of an xml parsing exception
	 */
	public PreparedSqlExecutePerformance(Element ePerformance) throws ParsingException {
		super(ePerformance);
	}

	@Override
	public ResultSet preform(SqlExecutor sqlExc, RequestArgumentStructureAssignment arguments, Credentials creds) throws SQLException {
		return sqlExc.executePreparedStatement(this.query, new StatementPreparerArgument(this.arguments, arguments, creds));
	}
}
