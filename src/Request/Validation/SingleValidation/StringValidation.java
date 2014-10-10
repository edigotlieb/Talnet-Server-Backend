/**
 * FILE : StringValidation.java AUTHORS : Idan Berkovits
 */
package Request.Validation.SingleValidation;

import Exceptions.ParsingException;
import Request.Argument.Argument;
import Request.Credentials;
import RequestArgumentAssignment.RequestArgumentStructureAssignment;
import SQL.SqlExecutor;
import java.sql.SQLException;
import org.w3c.dom.Element;

/**
 * implements a string validation set
 *
 * @author idanb55
 */
public class StringValidation extends SingleValidation {

	protected enum ValidationType {

		endsWith, equals, matches, isAlphaNumeric
	}
	protected StringValidation.ValidationType type;
	private String target;

	/**
	 * constructs a string special using an xml element
	 *
	 * @param eValidation the xml element
	 * @throws ParsingException thrown in case of an xml parsing exception
	 */
	public StringValidation(Element eValidation) throws ParsingException {
		super(eValidation);
		this.target = eValidation.getAttribute("target");
		this.type = StringValidation.ValidationType.valueOf(eValidation.getAttribute("type"));

	}

	@Override
	protected boolean helpValidate(SqlExecutor sqlExc, RequestArgumentStructureAssignment arguments, Credentials creds) throws SQLException {
		String targetValue = Argument.getValue(target, arguments, creds);
		switch (this.type) {
			case endsWith:
				return targetValue.endsWith(this.arguments.get(1).getValue(arguments, creds));
			case equals:
				return targetValue.equals(this.arguments.get(1).getValue(arguments, creds));
			case matches:
				return targetValue.matches(this.arguments.get(1).getValue(arguments, creds));
			case isAlphaNumeric:
				return Utilities.Sql.isAlphaNumeric(targetValue);
			default:
				return false;
		}
	}
}
