/**
 * FILE : PerformanceSet.java AUTHORS : Idan Berkovits
 */
package Request.Preformance;

import Exceptions.ExecutionException;
import Exceptions.ParsingException;
import Request.Credentials;
import RequestArgumentAssignment.RequestArgumentStructureAssignment;
import SQL.SqlExecutor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * implements a set of performance types
 *
 * @author idanb55
 */
public class PerformanceSet {

	private List<Performance> performances;

	/**
	 * adds a new performance to the set
	 *
	 * @param performance the performance to add to the set
	 */
	public final void addPerformance(Performance performance) {
		this.performances.add(performance);
	}

	/**
	 * constructs a performance set using an xml element
	 *
	 * @param ePerformance
	 * @throws ParsingException
	 */
	public PerformanceSet(Element ePerformance) throws ParsingException {
		performances = new LinkedList<>();
		NodeList nlPerformances = ePerformance.getChildNodes();
		for (int i = 0; i < nlPerformances.getLength(); i++) {
			if (nlPerformances.item(i).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			this.addPerformance(Performance.performanceFactory((Element) nlPerformances.item(i)));
		}
	}

	/**
	 * performs the all performance in set
	 *
	 * @param sqlExc
	 * @param arguments
	 * @param creds
	 * @return returns the first result set returned from a performance which
	 * its return attribute is set
	 * @throws ExecutionException thrown in case of sql exception
	 */
	public ResultSet preform(SqlExecutor sqlExc, RequestArgumentStructureAssignment arguments, Credentials creds) throws ExecutionException {
		ResultSet result = null;
		Iterator<Performance> perfomanceIt = performances.iterator();
		while (perfomanceIt.hasNext()) {
			try {
				Performance performance = perfomanceIt.next();
				if (result == null && performance.returnValue()) {
					result = performance.preform(sqlExc, arguments, creds);
				} else {
					performance.preform(sqlExc, arguments, creds);
				}
			} catch (SQLException ex) {
				throw new ExecutionException(ex);
			}
		}
		return result;
	}
}
