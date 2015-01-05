/**
 * FILE : AtleastValidation.java AUTHORS : Idan Berkovits
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
 * implements an atleast validation
 *
 * @author idanb55
 */
public class AtleastValidation extends MultipleValidation {

	private int demand;

	/**
	 * constructs an atleast validation using an xml element
	 *
	 * @param eValidation the xml element
	 * @throws ParsingException thrown in case of an xml parsing exception
	 */
	public AtleastValidation(Element eValidation) throws ParsingException {
		super(eValidation);
		if (eValidation.hasAttribute("demand")) {
			this.demand = Integer.parseInt(eValidation.getAttribute("demand"));
		} else {
			this.demand = 1;
		}

	}

	@Override
	public MultipleValidationType getType() {
		return MultipleValidation.MultipleValidationType.atleast;
	}

	@Override
	protected boolean helpValidate(SqlExecutor sqlExc, RequestArgumentStructureAssignment arguments, Credentials creds) throws SQLException, ValidationException {
		RequestArgumentListAssignment argumentList = (RequestArgumentListAssignment) arguments.getArgument(requestArgumentListName);
		Iterator<RequestArgumentStructureAssignment> argumentListIt = argumentList.iterator();
		int counter = 0; // counts how many elements were accepted
		while (argumentListIt.hasNext()) {
			RequestArgumentStructureAssignment requestArgumentStruct = argumentListIt.next();
			try {
				this.validationSet.validate(sqlExc, arguments.merge(requestArgumentStruct), creds);
				counter++;
				//if enough records were accepted, return true
				if (counter >= demand) {
					return true;
				}
			} catch (ValidationException e) { // keep checking in case of failure
			}
		}
		//if reached here, then the validation failed
		return false;
	}
}
