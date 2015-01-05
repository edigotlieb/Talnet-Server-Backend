/**
 * FILE : ExistenceValidation.java AUTHORS : Idan Berkovits
 */
package Request.Validation.SingleValidation;

import Exceptions.ParsingException;
import Request.Credentials;
import RequestArgumentAssignment.RequestArgumentStructureAssignment;
import SQL.PreparedStatements.StatementPreparerArgument;
import SQL.SqlExecutor;
import java.sql.SQLException;
import org.w3c.dom.Element;

/**
 * implements a existence validation set
 *
 * @author idanb55
 */
public class ExistenceValidation extends SingleValidation {

	protected enum ValidationType {

		PermissionGroupByName, AppByName, TableByName, UserByUsername
	}
	protected ExistenceValidation.ValidationType type;

	/**
	 * constructs a existence validation using an xml element
	 *
	 * @param eValidation the xml element
	 * @throws ParsingException thrown in case of an xml parsing exception
	 */
	public ExistenceValidation(Element eValidation) throws ParsingException {
		super(eValidation);
		this.type = ExistenceValidation.ValidationType.valueOf(eValidation.getAttribute("type"));
	}

	@Override
	protected boolean helpValidate(SqlExecutor sqlExc, RequestArgumentStructureAssignment arguments, Credentials creds) throws SQLException {
		switch (this.type) {
			case PermissionGroupByName:
				return !sqlExc.emptyResultExecutePreparedStatement("getPermissionGroupInfoByName", new StatementPreparerArgument(this.arguments, arguments, creds));
			case AppByName:
				return !sqlExc.emptyResultExecutePreparedStatement("getAllAppInfoByName", new StatementPreparerArgument(this.arguments, arguments, creds));
			case TableByName:
				return !sqlExc.emptyResultExecutePreparedStatement("getTableInfoByName", new StatementPreparerArgument(this.arguments, arguments, creds));
			case UserByUsername:
				return !sqlExc.emptyResultExecutePreparedStatement("getAllUserInfoByUsername", new StatementPreparerArgument(this.arguments, arguments, creds));
			default:
				return false;
		}
	}
}
