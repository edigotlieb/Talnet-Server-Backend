/**
 * FILE : Performance.java AUTHORS : Idan Berkovits
 */
package Request.Preformance;

import Exceptions.ExecutionException;
import Exceptions.ParsingException;
import Request.Argument.ArgumentSet;
import Request.Credentials;
import RequestArgumentAssignment.RequestArgumentStructureAssignment;
import SQL.SqlExecutor;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.w3c.dom.Element;

/**
 * implements a performance
 *
 * @author idanb55
 */
public abstract class Performance {

	/**
	 * returns a performance using a performance xml element
	 *
	 * @param ePerformance the performance xml element
	 * @return the performance instance describing the xml element
	 * @throws ParsingException thrown in case of xml parsing exception
	 */
	public static Performance performanceFactory(Element ePerformance) throws ParsingException {
		switch (ePerformance.getTagName()) {
			case "sqlExecute":
				return new PreparedSqlExecutePerformance(ePerformance);
			case "dynamicSqlExecute":
				return new DynamicSqlExecutePerformance(ePerformance);
			case "special":
				return new SpecialPerformance(ePerformance);
			case "foreach":
				return new ForeachPerformance(ePerformance);
			default:
				throw new ParsingException("Unkown performance " + ePerformance.getTagName());
		}
	}
	protected ArgumentSet arguments;
	protected String query;
	protected boolean returnValue;

	/**
	 * constructs a new Performance using a performance xml element
	 *
	 * @param ePerformance the performance element
	 * @throws ParsingException thrown in case of xml parsing exception
	 */
	public Performance(Element ePerformance) throws ParsingException {
		this.query = ePerformance.getAttribute("query");
		this.returnValue = ePerformance.hasAttribute("return") && ePerformance.getAttribute("return").equals("return");
		this.arguments = new ArgumentSet(ePerformance);
	}

	/**
	 * returns whether the permission is to be returned to the client
	 *
	 * @return whether the permission is to be returned to the client
	 */
	public boolean returnValue() {
		return returnValue;
	}

	/**
	 * performs the performance
	 *
	 * @param sqlExc
	 * @param arguments
	 * @param creds
	 * @return the resultSet returned in the request
	 * @throws ExecutionException thrown in case of an execution exception
	 * @throws SQLException thrown in case of an sql failure
	 */
	public abstract ResultSet preform(SqlExecutor sqlExc, RequestArgumentStructureAssignment arguments, Credentials creds) throws ExecutionException, SQLException;
}
