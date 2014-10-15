/**
 * FILE : SqlExecutor.java AUTHORS : Erez Gotlieb
 */
package SQL;

import SQL.DynamicStatements.SqlQueryGenerator;
import SQL.PreparedStatements.PreparedStatementStrings;
import SQL.PreparedStatements.StatementPreparer;
import SQL.PreparedStatements.StatementPreparerArgument;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * this class allows executing sql queries
 *
 * @author t7639192
 */
public class SqlExecutor {

	Connection con;
	static final Logger log = Logger.getGlobal();

	/**
	 * constructs a new SqlExecutor
	 *
	 * @param con the Connection to use
	 */
	public SqlExecutor(Connection con) {
		this.con = con;
	}

	/**
	 * executing a prepared statement
	 *
	 * @param sqlKey the sql key to retrieve the sql query
	 * @param sp StatementPreparer instance to set the arguments
	 * @return the resultSet returned from the execution
	 * @throws SQLException thrown in case of an sql failure
	 */
	public ResultSet executePreparedStatement(String sqlKey, StatementPreparer sp) throws SQLException {
		Logger.getGlobal().log(Level.FINE, "prepared statement - sqlKey: {0}", sqlKey);
		Logger.getGlobal().log(Level.FINE, "prepared statement - query: {0}", PreparedStatementStrings.getSQL(sqlKey));
		PreparedStatement ps = this.con.prepareStatement(PreparedStatementStrings.getSQL(sqlKey));
		sp.prepareStatement(ps);
		ps.execute();
		ResultSet rs = ps.getResultSet();
		if (rs == null) {
			ps.close();
		}
		return rs;
	}

	/**
	 * counts the number of rows returned from executing a prepared statement
	 *
	 * @param sqlKey the sql key to retrieve the sql query
	 * @param sp StatementPreparer instance to set the arguments
	 * @return the number of rows returned from the execution
	 * @throws SQLException thrown in case of an sql failure
	 */
	public int countExecutePreparedStatement(String sqlKey, StatementPreparer sp) throws SQLException {
		ResultSet rs = this.executePreparedStatement(sqlKey, sp);
		rs.afterLast();
		return rs.getRow();
	}

	/**
	 * returns whether the result returned from executing a prepared statement
	 * is empty
	 *
	 * @param sqlKey the sql key to retrieve the sql query
	 * @param sp StatementPreparer instance to set the arguments
	 * @return true if the result returned from executing a prepared statement
	 * is empty, otherwise, false
	 * @throws SQLException thrown in case of an sql failure
	 */
	public boolean emptyResultExecutePreparedStatement(String sqlKey, StatementPreparer sp) throws SQLException {
		return this.countExecutePreparedStatement(sqlKey, sp) <= 0;
	}

	/**
	 * executes a dynamic query
	 *
	 * @param query determines what query to execute. if sp parameter is null
	 * query needs to be th actual query to execute. otherwise, the query would
	 * be generated using SqlQueryGenerator.
	 * @param sp sets the argument for the dynamic query, or if it null, sets
	 * the query to be the precise query t execute.
	 * @return the resultSet returned fro the execution
	 * @throws SQLException thrown in case of an sql execution exception
	 */
	public ResultSet executeDynamicStatementQry(String query, StatementPreparerArgument sp) throws SQLException {
		Logger.getGlobal().log(Level.FINE, "dynamic statement - queryKey: {0}", query);
		Statement stmt = con.createStatement();
		String sql;
		//retrives the sql query
		if (sp != null) {
			sql = SqlQueryGenerator.getQueryGenerator(query).generateQuery(sp);
		} else {
			sql = query;
		}
		Logger.getGlobal().log(Level.FINE, "dynamic statement - query: {0}", sql);
		if (sql.startsWith("INSERT")) {
			//get generated keys if inserted
			stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			return stmt.getGeneratedKeys();
		}
		stmt.execute(sql);
		return stmt.getResultSet();
	}
}
