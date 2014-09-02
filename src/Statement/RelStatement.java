
package Statement;

import SQL.Utilities.Utils;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Defines an abstract relational SQL WHERE statement
 */
public class RelStatement extends Statement {

        // the  tow terms and the operand defining the statement
	private final String col, value, op;
        
        // the list of allowed opperands
	private static final List<String> operands = new ArrayList<>();

	static {

		RelStatement.operands.add("=");
		RelStatement.operands.add("!=");
		RelStatement.operands.add(">=");
		RelStatement.operands.add("<=");
		RelStatement.operands.add(">");
		RelStatement.operands.add("<");
		RelStatement.operands.add("LIKE");
		RelStatement.operands.add("NOT LIKE");
		RelStatement.operands.add("NOT NULL");
	}

        /**
         * Default constructor
         * 
         * @param col the first term
         * @param value the second term
         * @param op the operand 
         */
	public RelStatement(String col, String value, String op) {
		this.col = Utils.sanitizeAlphaNumeric(col);
		this.value = Utils.sanitizeSqlCharacterEscaping(value);
		this.op = op;
	}

	@Override
	public boolean isColumnIn(String colname) {
		return col.equals(colname);
	}

        
	@Override
        /**
         * validated the operand is legal
         */
	public boolean validateOperands() {
		return operands.contains(op);
	}

	@Override
	public String toString() {
		return col + " " + op + " '" + value + "'";
	}
}
