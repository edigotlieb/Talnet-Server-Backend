/**
 * FILE : StatementValidation.java AUTHORS : Idan Berkovits
 */
package Request.Validation.SingleValidation;

import Exceptions.ParsingException;
import Request.Credentials;
import RequestArgumentAssignment.RequestArgumentStructureAssignment;
import SQL.SqlExecutor;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.Element;

/**
 * implements a statement validation set
 *
 * @author idanb55
 */
public class MapValidation extends SingleValidation {

	protected enum ValidationType {

		areKeysAlphaNumeric, isEmpty, containsKey
	}
	protected MapValidation.ValidationType type;
	private String target;

	/**
	 * constructs a statement special using an xml element
	 *
	 * @param eValidation the xml element
	 * @throws ParsingException thrown in case of an xml parsing exception
	 */
	public MapValidation(Element eValidation) throws ParsingException {
		super(eValidation);
		this.target = eValidation.getAttribute("target");
		if (!this.target.matches("\\{[\\w]*\\}")) {
			throw new ParsingException("Illegal Statement target");
		}
		this.type = MapValidation.ValidationType.valueOf(eValidation.getAttribute("type"));
	}

	@Override
	protected boolean helpValidate(SqlExecutor sqlExc, RequestArgumentStructureAssignment arguments, Credentials creds) throws SQLException {
		Map<String, String> map = (Map<String, String>) arguments.getArgument(this.target).getValue();
		switch (this.type) {
			case areKeysAlphaNumeric:
				for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
					if (!Utilities.Sql.isAlphaNumeric(it.next())) {
						return false;
					}
				}
				return true;
			case isEmpty:
				return map.isEmpty();
			case containsKey:
				return map.containsKey(this.arguments.get(1).getValue(arguments, creds));
			default:
				return false;
		}
	}
}
