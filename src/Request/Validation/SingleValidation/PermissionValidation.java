/**
 * FILE : PermissionValidation.java AUTHORS : Idan Berkovits
 */
package Request.Validation.SingleValidation;

import Exceptions.ParsingException;
import Request.Argument.Argument;
import Request.Credentials;
import RequestArgumentAssignment.RequestArgumentStructureAssignment;
import SQL.PreparedStatements.StatementPreparerArgument;
import SQL.SqlExecutor;
import java.sql.SQLException;
import org.w3c.dom.Element;

/**
 * implements a permission validation set
 *
 * @author idanb55
 */
public class PermissionValidation extends SingleValidation {

	protected enum ValidationType {

		TablePermission, TablePermissionUser, PermissionGroupAdmin, UserInGroup, AnyPermissionGroupAdmin
	}
	protected PermissionValidation.ValidationType type;

	/**
	 * constructs a permission validation using an xml element
	 *
	 * @param eValidation the xml element
	 * @throws ParsingException thrown in case of an xml parsing exception
	 */
	public PermissionValidation(Element eValidation) throws ParsingException {
		super(eValidation);
		this.type = PermissionValidation.ValidationType.valueOf(eValidation.getAttribute("type"));
	}

	@Override
	protected boolean helpValidate(SqlExecutor sqlExc, RequestArgumentStructureAssignment arguments, Credentials creds) throws SQLException {
		switch (this.type) {
			case TablePermission:
				return !sqlExc.emptyResultExecutePreparedStatement("getTablePermissionForGroup", new StatementPreparerArgument(this.arguments, arguments, creds));
			case TablePermissionUser:
				if (!sqlExc.emptyResultExecutePreparedStatement("getUserTablePermission", new StatementPreparerArgument(this.arguments, arguments, creds))) {
					if(this.arguments.get(4).getValue(arguments, creds).equals(creds.getAppName())){
						return true;
					}
					return creds.isMasterApplication() && creds.isAppSuperAdmin(this.arguments.get(4).getValue(arguments, creds));
				} else {
					return false;
				}
			case PermissionGroupAdmin:
				return !sqlExc.emptyResultExecutePreparedStatement("getPermissionGroupInfoByNameAndAdminname", new StatementPreparerArgument(this.arguments, arguments, creds));
			case UserInGroup:
				return !sqlExc.emptyResultExecutePreparedStatement("getUserPermissionGroup", new StatementPreparerArgument(this.arguments, arguments, creds));
			case AnyPermissionGroupAdmin:
				return !sqlExc.emptyResultExecutePreparedStatement("getUserPermissionGroupsAdmin", new StatementPreparerArgument(this.arguments, arguments, creds));
			default:
				return false;
		}
	}
}
