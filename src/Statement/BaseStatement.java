/**
 * FILE : BaseStatement.java AUTHORS : Erez Gotlieb
 */
package Statement;

import Utilities.Sql;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Defines an abstract relational SQL WHERE statement
 */
public class BaseStatement extends Statement {

	// the  tow terms and the operand defining the statement
	private final String col, value, op;
	// the list of allowed opperands
	private static final List<String> operands = new ArrayList<>();

	static {
		BaseStatement.operands.add("=");
		BaseStatement.operands.add("!=");
		BaseStatement.operands.add(">=");
		BaseStatement.operands.add("<=");
		BaseStatement.operands.add(">");
		BaseStatement.operands.add("<");
		BaseStatement.operands.add("LIKE");
		BaseStatement.operands.add("NOT LIKE");
		BaseStatement.operands.add("NOT NULL");
	}

	/**
	 * Default constructor
	 *
	 * @param col the first term
	 * @param value the second term
	 * @param op the operand
	 */
	public BaseStatement(String col, Object value, String op) {
		this.col = Sql.sanitizeAlphaNumeric(col);
		if (value != null) {
			this.value = Sql.sanitizeSqlCharacterEscaping(value.toString());
		} else {
			this.value = null;
		}
		this.op = op;
	}

	@Override
	public boolean isColumnIn(String colname) {
		return col.equals(colname);
	}

	@Override
	public boolean validateOperands() {
		return operands.contains(op);
	}

	@Override
	public String toString() {
		if (value != null) {
			return col + " " + op + " '" + value + "'";
		} else {
			return col + " " + op + " NULL";
		}
	}
}