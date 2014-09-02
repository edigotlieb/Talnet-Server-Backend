/** 
 * FILE : StatementPreparer.java
 * AUTHORS : Erez Gotlieb    
 * DESCRIPTION : 
 */ 

package SQL.PreparedStatements;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class StatementPreparer {

    public abstract void prepareStatement(PreparedStatement ps) throws SQLException;
}
