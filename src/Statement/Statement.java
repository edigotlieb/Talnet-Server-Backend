package Statement;

/**
 * This class defines an abstract SQL WHERE statement allowed in our system 
 */
abstract public class Statement {

    
	public static Statement StatementFactory(String statement) {
		return null;
	}

	abstract public boolean isColumnIn(String colname);
	abstract public boolean validateOperands();

	@Override
	abstract public String toString();
}
