
package Statement;

/**
 *  Defines an abstract relational OR SQL WHERE statement
 */
public class OrStatement extends Statement {

        // the two statements that define the OR
	private final Statement st1, st2;

        /**
         * Default Constructor
         * 
         * @param st1 the first statement
         * @param st2 the second statement
         */
	public OrStatement(Statement st1, Statement st2) {
		this.st1 = st1;
		this.st2 = st2;
	}

	@Override
	public String toString() {
		return "(" + st1.toString() + ") OR (" + st2.toString() + ")";
	}

	@Override
	public boolean isColumnIn(String colname) {
		return st1.isColumnIn(colname) || st2.isColumnIn(colname);
	}

	@Override
        /**
         * validates the operands of each statement
         */
	public boolean validateOperands() {
		return st1.validateOperands() && st2.validateOperands();
	}
}
