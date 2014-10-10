/**
 * FILE : SingleValidation.java AUTHORS : Idan Berkovits
 */
package Request.Validation.SingleValidation;

import Exceptions.ParsingException;
import Request.Argument.ArgumentSet;
import Request.Validation.Validation;
import org.w3c.dom.Element;

/**
 * implements a single validation set
 *
 * @author idanb55
 */
public abstract class SingleValidation extends Validation {

	protected ArgumentSet arguments;

	/**
	 * constructs a single validation using an xml element
	 *
	 * @param eValidation the xml element
	 * @throws ParsingException thrown in case of an xml parsing exception
	 */
	public SingleValidation(Element eValidation) throws ParsingException {
		super(eValidation);
		this.arguments = new ArgumentSet(eValidation);
	}
}
