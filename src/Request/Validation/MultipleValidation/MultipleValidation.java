/**
 * FILE : MultipleValidation.java AUTHORS : Idan Berkovits
 */
package Request.Validation.MultipleValidation;

import Exceptions.ParsingException;
import Request.Validation.Validation;
import Request.Validation.ValidationSet;
import org.w3c.dom.Element;

/**
 * implements a multiple validation
 *
 * @author idanb55
 */
public abstract class MultipleValidation extends Validation {

	protected String requestArgumentListName;
	protected ValidationSet validationSet;

	public enum MultipleValidationType {

		foreach, atleast
	}

	/**
	 * constructs a multiple validation using the xml element
	 *
	 * @param eValidation
	 * @throws ParsingException
	 */
	public MultipleValidation(Element eValidation) throws ParsingException {
		super(eValidation);
		this.requestArgumentListName = eValidation.getAttribute("argname");
		this.validationSet = new ValidationSet(eValidation);
	}

	/**
	 * returns the type of the validation
	 * @return the type of the validation
	 */
	public abstract MultipleValidationType getType();
}
