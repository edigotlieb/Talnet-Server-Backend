/**
 * FILE : CredentialsValidation.java AUTHORS : Idan Berkovits
 */
package Request.Validation.SingleValidation;

import Exceptions.ParsingException;
import Request.Credentials;
import RequestArgumentAssignment.RequestArgumentStructureAssignment;
import SQL.SqlExecutor;
import java.sql.SQLException;
import org.w3c.dom.Element;

/**
 * implements a credentials validation set
 *
 * @author idanb55
 */
public class CredentialsValidation extends SingleValidation {

	protected enum ValidationType {

		isDeveloper, isMasterApplication, AppAdmin
	}
	protected CredentialsValidation.ValidationType type;

	/**
	 * constructs a credentials validation using an xml element
	 *
	 * @param eValidation the xml element
	 * @throws ParsingException thrown in case of an xml parsing exception
	 */
	public CredentialsValidation(Element eValidation) throws ParsingException {
		super(eValidation);
		this.type = CredentialsValidation.ValidationType.valueOf(eValidation.getAttribute("type"));
	}

	@Override
	protected boolean helpValidate(SqlExecutor sqlExc, RequestArgumentStructureAssignment arguments, Credentials creds) throws SQLException {
		switch (this.type) {
			case isDeveloper:
				return creds.isDeveloper();
			case isMasterApplication:
				return creds.isMasterApplication();
			case AppAdmin:
				return creds.isAppSuperAdmin(this.arguments.get(1).getValue(arguments, creds));
			default:
				return false;
		}
	}
}
