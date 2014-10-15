/**
 * FILE : SpecialValidation.java AUTHORS : Idan Berkovits
 */
package Request.Validation.SingleValidation;

import Exceptions.ParsingException;
import Request.Credentials;
import RequestArgumentAssignment.RequestArgumentStructureAssignment;
import SQL.PreparedStatements.StatementPreparer;
import SQL.SqlExecutor;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.w3c.dom.Element;

/**
 * implements a special validation set
 *
 * @author idanb55
 */
public class SpecialValidation extends SingleValidation {

	protected enum ValidationType {

		selfOrGroupAdmin
	}
	protected SpecialValidation.ValidationType type;

	/**
	 * constructs a permission special using an xml element
	 *
	 * @param eValidation the xml element
	 * @throws ParsingException thrown in case of an xml parsing exception
	 */
	public SpecialValidation(Element eValidation) throws ParsingException {
		super(eValidation);
		this.type = SpecialValidation.ValidationType.valueOf(eValidation.getAttribute("type"));

	}

	@Override
	public boolean helpValidate(SqlExecutor sqlExc, RequestArgumentStructureAssignment arguments, Credentials creds) throws SQLException {
		switch (this.type) {
			case selfOrGroupAdmin:
				final String groupname = this.arguments.get(1).getValue(arguments, creds);
				final String firstuser = creds.getUsername();
				final String userToRemove = this.arguments.get(2).getValue(arguments, creds);

				return !(sqlExc.emptyResultExecutePreparedStatement("getPermissionGroupInfoByNameAndAdminname", new StatementPreparer() {
					@Override
					public void prepareStatement(PreparedStatement ps) throws SQLException {
						ps.setString(1, groupname);
						ps.setString(2, firstuser);
					}
				}) && !firstuser.equals(userToRemove));
			default:
				return false;
		}
	}
}
