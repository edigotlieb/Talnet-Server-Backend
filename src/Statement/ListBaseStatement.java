/**
 * FILE : RelStatement.java AUTHORS : Erez Gotlieb
 */
package Statement;

import Utilities.Sql;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Defines an abstract relational SQL WHERE statement
 */
public class ListBaseStatement extends Statement {

	// the  tow terms and the operand defining the statement
	private final String col, op;
	private final String[] value;
	// the list of allowed opperands
	private static final List<String> operands = new ArrayList<>();

	static {
		ListBaseStatement.operands.add("IN");
		ListBaseStatement.operands.add("NOT IN");
	}

	/**
	 * Default constructor
	 *
	 * @param col the first term
	 * @param value the second term
	 * @param op the operand
	 */
	public ListBaseStatement(String col, Object[] values, String op) {
		this.col = Sql.sanitizeAlphaNumeric(col);
		if (values != null) {
			this.value = new String[values.length];
			for (int i = 0; i < this.value.length; i++) {
				this.value[i] = Sql.sanitizeSqlCharacterEscaping(values[i].toString());
			}
		} else {
			this.value = new String[0];
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
		String values = "";
		for (int i = 0; i < this.value.length; i++) {
			values += "'" + this.value[i].toString() + "'";
			if (i < this.value.length - 1) {
				values = values + ", ";
			}
		}
		return col + " " + op + " (" + values + ")";

	}
}
