/**
 * FILE : ForeachValidation.java AUTHORS : Idan Berkovits
 */
package Request.Validation.MultipleValidation;

import Exceptions.ParsingException;
import Request.Credentials;
import Exceptions.ValidationException;
import RequestArgumentAssignment.RequestArgumentListAssignment;
import RequestArgumentAssignment.RequestArgumentStructureAssignment;
import SQL.SqlExecutor;
import java.sql.SQLException;
import java.util.Iterator;
import org.w3c.dom.Element;

/**
 * implements a foreach validation
 *
 * @author idanb55
 */
public class ForeachValidation extends MultipleValidation {

	/**
	 * constructs a foreach validation using an xml element
	 *
	 * @param eValidation the xml element
	 * @throws ParsingException thrown in case of an xml parsing exception
	 */
	public ForeachValidation(Element eValidation) throws ParsingException {
		super(eValidation);
	}

	@Override
	public MultipleValidationType getType() {
		return MultipleValidation.MultipleValidationType.foreach;
	}

	@Override
	protected boolean helpValidate(SqlExecutor sqlExc, RequestArgumentStructureAssignment arguments, Credentials creds) throws SQLException, ValidationException {
		RequestArgumentListAssignment argumentList = (RequestArgumentListAssignment) arguments.getArgument(requestArgumentListName);
		Iterator<RequestArgumentStructureAssignment> argumentListIt = argumentList.iterator();
		while (argumentListIt.hasNext()) {
			RequestArgumentStructureAssignment requestArgumentStruct = argumentListIt.next();
			this.validationSet.validate(sqlExc, arguments.merge(requestArgumentStruct), creds);
		}
		return true;
	}
}
