/**
 * FILE : ForeachPerformance.java AUTHORS : Idan Berkovits
 */
package Request.Preformance;

import Exceptions.ExecutionException;
import Exceptions.ParsingException;
import Request.Credentials;
import RequestArgumentAssignment.RequestArgumentListAssignment;
import RequestArgumentAssignment.RequestArgumentStructureAssignment;
import SQL.SqlExecutor;
import java.sql.ResultSet;
import java.util.Iterator;
import org.w3c.dom.Element;

/**
 * implements a foreach type performance
 *
 * @author idanb55
 */
public class ForeachPerformance extends Performance {

	protected String requestArgumentListName;
	protected PerformanceSet performanceSet;

	/**
	 * constructs a new foreach performance using an xml element
	 *
	 * @param ePerformance the foreach xml element
	 * @throws ParsingException thrown in case of a parsing exception
	 */
	public ForeachPerformance(Element ePerformance) throws ParsingException {
		super(ePerformance);
		requestArgumentListName = ePerformance.getAttribute("argname");
		performanceSet = new PerformanceSet(ePerformance);
	}

	/**
	 * preforms the performanceSet for each element in the request argument list
	 * returns the first resultSet returns in the execution
	 *
	 * @param sqlExc
	 * @param arguments
	 * @param creds
	 * @return the first resultSet returns in the execution
	 * @throws ExecutionException thrown in case of an execution exception
	 */
	@Override
	public ResultSet preform(SqlExecutor sqlExc, RequestArgumentStructureAssignment arguments, Credentials creds) throws ExecutionException {
		ResultSet result = null;
		RequestArgumentListAssignment argumentList = (RequestArgumentListAssignment) arguments.getArgument(requestArgumentListName);
		Iterator<RequestArgumentStructureAssignment> argumentListIt = argumentList.iterator();
		while (argumentListIt.hasNext()) {
			RequestArgumentStructureAssignment requestArgumentStruct = argumentListIt.next();
			if (result == null) {
				result = this.performanceSet.preform(sqlExc, arguments.merge(requestArgumentStruct), creds);
			} else {
				this.performanceSet.preform(sqlExc, arguments.merge(requestArgumentStruct), creds);
			}
		}
		return result;
	}
}
