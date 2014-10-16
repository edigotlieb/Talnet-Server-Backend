/**
 * FILE : Validation.java AUTHORS : Idan Berkovits
 */
package Request.Validation;

import Exceptions.ParsingException;
import Request.Credentials;
import Exceptions.ValidationException;
import Request.Validation.MultipleValidation.AtleastValidation;
import Request.Validation.MultipleValidation.ForeachValidation;
import Request.Validation.SingleValidation.CredentialsValidation;
import Request.Validation.SingleValidation.ExistenceValidation;
import Request.Validation.SingleValidation.PermissionValidation;
import Request.Validation.SingleValidation.SpecialValidation;
import Request.Validation.SingleValidation.StatementValidation;
import Request.Validation.SingleValidation.StringValidation;
import RequestArgumentAssignment.RequestArgumentStructureAssignment;
import SQL.SqlExecutor;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;

/**
 * implements a validation
 *
 * @author idanb55
 */
public abstract class Validation {

	private boolean gate;
	private int errorid;

	/**
	 * constructs a validation using an xml element
	 *
	 * @param eValidation the validation xml element
	 */
	public Validation(Element eValidation) {
		this.errorid = Integer.parseInt(eValidation.getAttribute("errorid"));
		this.gate = true;
		if (eValidation.hasAttribute("gate")) {
			switch (eValidation.getAttribute("gate").toLowerCase()) {
				case "not":
					this.gate = false;
				case "straight":
				default:
					this.gate = true;
			}
		}
	}

	/**
	 * creates a new validation using an xml validation element
	 *
	 * @param eValidation the xml validation element
	 * @return the created validation
	 * @throws ParsingException thrown in case of an xml parsing exception
	 */
	public static Validation validationFactory(Element eValidation) throws ParsingException {
		switch (eValidation.getTagName()) {
			case "existence":
				return new ExistenceValidation(eValidation);
			case "credentials":
				return new CredentialsValidation(eValidation);
			case "permission":
				return new PermissionValidation(eValidation);
			case "string":
				return new StringValidation(eValidation);
			case "statement":
				return new StatementValidation(eValidation);
			case "special":
				return new SpecialValidation(eValidation);
			case "foreach":
				return new ForeachValidation(eValidation);
			case "atleast":
				return new AtleastValidation(eValidation);
			default:
				throw new ParsingException("Unkown Validation " + eValidation.getTagName());
		}
	}

	/**
	 * a function to validate the specific validation type
	 *
	 * @param sqlExc
	 * @param arguments
	 * @param creds
	 * @return true if the validation was accepted, otherwise, false
	 * @throws SQLException thrown in case of a sql executing exception
	 * @throws ValidationException thrown in case where the validation fails
	 */
	protected abstract boolean helpValidate(SqlExecutor sqlExc, RequestArgumentStructureAssignment arguments, Credentials creds) throws SQLException, ValidationException;

	/**
	 * this function validates the validation type
	 *
	 * @param sqlExc
	 * @param arguments
	 * @param creds
	 * @return true if the validation was accepted, otherwise, false, although
	 * it never returns false, as in case of a failure a ValidationException
	 * would be thrown.
	 * @throws ValidationException thrown in case where the validation fails
	 * @throws SQLException thrown in case of a sql executing exception
	 */
	public boolean validate(SqlExecutor sqlExc, RequestArgumentStructureAssignment arguments, Credentials creds) throws ValidationException, SQLException {
		boolean flag = this.helpValidate(sqlExc, arguments, creds);
		Logger.getGlobal().log(Level.FINE, "DEBUG help val {0}", flag);
		Logger.getGlobal().log(Level.FINE, "DEBUG gate {0}", this.gate);
		if ((this.gate && !flag) || (!this.gate && flag)) {
			throw new ValidationException(this.errorid);
		}
		return true;
	}
}
