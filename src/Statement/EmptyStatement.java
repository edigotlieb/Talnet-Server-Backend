package Statement;

/**
 * Defines an abstract empty SQL WHERE statement
 *
 */
public class EmptyStatement extends Statement {


	@Override
	public String toString() {
		return "1";
	}

	@Override
	public boolean isColumnIn(String colname) {
		return false;
	}

	@Override
	public boolean validateOperands() {
		return true;
	}
}
