
import Utilities.RuntimeParams;
import SQL.SqlExecutor;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author edi
 */
public class GarbageKeyCollector extends TimerTask{

    ComboPooledDataSource ds;
    final int lifeTime;
    
    
    public GarbageKeyCollector(ComboPooledDataSource ds) {      
        super();
        this.ds = ds;
        lifeTime =(int) RuntimeParams.getParams("keyLifeTime"); // in minutes
    }    
    
    @Override
    public void run() {
        
        Logger.getGlobal().log(Level.INFO, "Running GarbageKeyCollector...");
        
        try (Connection con = ds.getConnection()){           
            
            SqlExecutor sqlExec = new SqlExecutor(con);
            
            con.close();
            
        } catch (SQLException ex) {
            
        }
        
        
        
    }
    
    
    
}
