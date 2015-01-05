/**
 * FILE : ValidationSet.java AUTHORS : Idan Berkovits
 */
package Request.Validation;

import Exceptions.ParsingException;
import Request.Credentials;
import Exceptions.ValidationException;
import RequestArgumentAssignment.RequestArgumentStructureAssignment;
import SQL.SqlExecutor;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * implements a validation set
 *
 * @author idanb55
 */
public class ValidationSet {

	private List<Validation> validations;

	/**
	 * adds a validation to the validation collection
	 *
	 * @param validation the validation to add to the collection
	 */
	public final void addValidation(Validation validation) {
		this.validations.add(validation);
	}

	/**
	 * constructs a set of validation using a wrapping xml element
	 *
	 * @param eValidation the wrapping element
	 * @throws ParsingException thrown in case of an xml parsing exception
	 */
	public ValidationSet(Element eValidation) throws ParsingException {
		validations = new LinkedList<>();
		NodeList nlValidations = eValidation.getChildNodes();
		for (int i = 0; i < nlValidations.getLength(); i++) {
			if (nlValidations.item(i).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			this.addValidation(Validation.validationFactory((Element) nlValidations.item(i)));
		}
	}

	/**
	 * validates every validation member in the set
	 *
	 * @param sqlExc
	 * @param arguments
	 * @param creds
	 * @return true if every validation was accepted
	 * @throws ValidationException thrown in case of validation failure
	 * @throws SQLException thrown in case of an sql query execution failure
	 */
	public boolean validate(SqlExecutor sqlExc, RequestArgumentStructureAssignment arguments, Credentials creds) throws ValidationException, SQLException {
		Iterator<Validation> validationIt = this.iterator();
		while (validationIt.hasNext()) {
			Validation validation = validationIt.next();
			validation.validate(sqlExc, arguments, creds);
		}
		return true;
	}

	/**
	 * get a validation using an index
	 * @param index the index to use
	 * @return the validation instance
	 */
	public Validation get(int index) {
		return validations.get(index);
	}

	/**
	 * returns the size of the set
	 * @return the size of the set
	 */
	public int getSize() {
		return validations.size();
	}

	/**
	 * returns a validation iterator to iterate over the validation
	 * @return the validation iterator
	 */
	public Iterator<Validation> iterator() {
		return validations.iterator();
	}
}
