/**
 * FILE : StatementValidation.java AUTHORS : Idan Berkovits
 */
package Request.Validation.SingleValidation;

import Exceptions.ParsingException;
import Request.Credentials;
import RequestArgumentAssignment.RequestArgumentStructureAssignment;
import SQL.SqlExecutor;
import Statement.Statement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;

/**
 * implements a statement validation set
 *
 * @author idanb55
 */
public class StatementValidation extends SingleValidation {

	protected enum ValidationType {

		isColumnIn, validateOperands
	}
	protected StatementValidation.ValidationType type;
	private String target;

	/**
	 * constructs a statement special using an xml element
	 *
	 * @param eValidation the xml element
	 * @throws ParsingException thrown in case of an xml parsing exception
	 */
	public StatementValidation(Element eValidation) throws ParsingException {
		super(eValidation);
		this.target = eValidation.getAttribute("target");
		if (!this.target.matches("\\{[\\w]*\\}")) {
			throw new ParsingException("Illegal Statement target");
		}
		this.type = StatementValidation.ValidationType.valueOf(eValidation.getAttribute("type"));
	}

	@Override
	protected boolean helpValidate(SqlExecutor sqlExc, RequestArgumentStructureAssignment arguments, Credentials creds) throws SQLException {
		Statement st = (Statement) arguments.getArgument(this.target).getValue();
		switch (this.type) {
			case isColumnIn:
				return st.isColumnIn(this.arguments.get(1).getValue(arguments, creds));
			case validateOperands:
				return st.validateOperands();
			default:
				return false;
		}
	}
}
