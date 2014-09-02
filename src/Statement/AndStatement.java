/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Statement;

/**
 *
 * Defines an abstract relational AND SQL WHERE statement
 */
public class AndStatement extends Statement{

        // the two statements that define the AND
	private final Statement st1, st2;

        /**
         * Default Constructor
         * 
         * @param st1 the first statement
         * @param st2 the second statement
         */
	public AndStatement(Statement st1, Statement st2) {
		this.st1 = st1;
		this.st2 = st2;
	}
	
        @Override
	public String toString(){
		return "(" + st1.toString() + ") AND (" + st2.toString() + ")";
	}

	@Override
	public boolean isColumnIn(String colname) {
		return st1.isColumnIn(colname) || st2.isColumnIn(colname);
	}

	@Override
	public boolean validateOperands() {
		return st1.validateOperands() && st2.validateOperands();
	}
}
