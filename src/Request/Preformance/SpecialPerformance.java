/**
 * FILE : SpecialPerformance.java AUTHORS : Idan Berkovits
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
 * a special execution performance type
 *
 * @author idanb55
 */
public class SpecialPerformance extends Performance {

	/**
	 * constructs a special execution performance using an xml element
	 *
	 * @param ePerformance the performance xml element
	 * @throws ParsingException thrown in case of an xml parsing exception
	 */
	public SpecialPerformance(Element ePerformance) throws ParsingException {
		super(ePerformance);
		this.queryType = SpecialPerformance.PerformanceQuery.valueOf(ePerformance.getAttribute("query"));
	}

	protected enum PerformanceQuery {

		DropAppTables
	}
	protected SpecialPerformance.PerformanceQuery queryType;

	@Override
	public ResultSet preform(SqlExecutor sqlExc, RequestArgumentStructureAssignment arguments, Credentials creds) throws SQLException {
		switch (this.queryType) {
			case DropAppTables:
				ResultSet rset = sqlExc.executePreparedStatement("getAppTables", new StatementPreparerArgument(this.arguments, arguments, creds));
				String sql = "DROP TABLE ";
				int counter = 0;
				while (rset.next()) {
					sql += this.arguments.get(1).getValue(arguments, creds) + "_" + rset.getString("TABLENAME") + ", ";
					counter++;
				}
				if (counter > 0) {
					sql = sql.substring(0, sql.length() - 2);
				}
				sqlExc.executeDynamicStatementQry(sql, null);
			default:
				return null;
		}
	}
}
