/**
 * FILE : DynamicSqlExecutePerformance.java AUTHORS : Idan Berkovits
 */
package Request.Preformance;

import Exceptions.ParsingException;
import Request.Credentials;
import RequestArgumentAssignment.RequestArgumentStructureAssignment;
import SQL.PreparedStatements.StatementPreparerArgument;
import SQL.SqlExecutor;
import Utilities.Sql;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.w3c.dom.Element;

/**
 * a dynamic sql execution performance type
 *
 * @author idanb55
 */
public class DynamicSqlExecutePerformance extends Performance {

	public static List<String> selectUserColumns;

	/**
	 * constructs a dynamic sql execution performance using an xml element
	 *
	 * @param ePerformance the performance xml element
	 * @throws ParsingException thrown in case of an xml parsing exception
	 */
	public DynamicSqlExecutePerformance(Element ePerformance) throws ParsingException {
		super(ePerformance);
	}

	@Override
	public ResultSet preform(SqlExecutor sqlExc, RequestArgumentStructureAssignment arguments, Credentials creds) throws SQLException {
		if (DynamicSqlExecutePerformance.selectUserColumns == null) {
			DynamicSqlExecutePerformance.selectUserColumns = Sql.getColNames(sqlExc, "USERS");
			selectUserColumns.remove("PASSWORD");
			selectUserColumns.add("CONCAT_WS(' ',USERS.FIRST_NAME,USERS.LAST_NAME) AS NAME");
		}
		return sqlExc.executeDynamicStatementQry(this.query, new StatementPreparerArgument(this.arguments, arguments, creds));
	}
}
