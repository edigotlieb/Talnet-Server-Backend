
import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/** 
 * FILE : ThreadHandler.java
 AUTHORS : Erez Gotlieb    
 DESCRIPTION : This class manages the serverSocket and ClientRequestThreads
 *             That were built to handle client requests
 */ 
public class ThreadHandler {
    
    // a flag - has thet handler been initiallized
    private static boolean wasInit = false;
    
    // and array to hold all the active threads
    private static ArrayList<ClientRequestThread> threads;  
    
    // the server socket
    private static ServerSocket ss;
    
    // a pooled data source (C3P0) of DB connections
    private static  ComboPooledDataSource ds;
    
    // the logger being used and it's file handler
    private static final Logger logger = Logger.getGlobal();
    private static FileHandler fh;
    
    // a timer used for garbage collection tasks
    private static Timer garbageKeyCollectorTimer;
        
    /**
     * An initialization function
     * 
     * @param paramFileName name of the parameter file (or null if default params) 
     * @return true if init was successful, false - otherwise
     */
    public static boolean init(String paramFileName) {
        
        garbageKeyCollectorTimer = new Timer();
        
        
        try {            
            RuntimeParams.readParams(paramFileName);       // read all the params from a config file
        } catch (Exception ex) {
            // cant open param file
            logger.log(Level.SEVERE, "Can't open parameter file");
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            logger.log(Level.SEVERE, "closing...");
            System.exit(0);
            // exit
        }
        
        threads = new ArrayList<>();
        
        try {
            
            // init the server socket
            ss = new ServerSocket((int) RuntimeParams.getParams("RequestPort"), (int) RuntimeParams.getParams("SocketBackLog"));
            
            ds = new ComboPooledDataSource();
            // init the data source
            ds.setDriverClass(String.valueOf(RuntimeParams.getParams("DriverClass")));
            ds.setUser(String.valueOf(RuntimeParams.getParams("DBUser")));
            ds.setPassword(String.valueOf(RuntimeParams.getParams("DBPass")));
            ds.setJdbcUrl(String.valueOf(RuntimeParams.getParams("DBURL")));
            ds.setMaxPoolSize((int) RuntimeParams.getParams("MaxPoolSize"));
            ds.setMaxStatementsPerConnection((int) RuntimeParams.getParams("MaxStatementsPerConnection"));
            ds.setMaxIdleTime((int) RuntimeParams.getParams("MaxIdleTime"));                     
                             
            ds.getConnection();
            
            fh = new FileHandler((String)RuntimeParams.getParams("LogFileName"));
            fh.setFormatter(new SimpleFormatter());
            
        } catch (IOException | PropertyVetoException | SQLException ex) {
            // cant start the server socket or open param file or set params of the DS
            // or cant open log file
            logger.log(Level.SEVERE, "Failed on port or DB init");
            logger.log(Level.SEVERE, ex.getMessage(), ex);       
            logger.log(Level.SEVERE, "closing...");
             System.exit(1);
        } 
        
      //  garbageKeyCollectorTimer.scheduleAtFixedRate(new GarbageKeyCollector(ds), 100, (long) RuntimeParams.getParams("GarbageKeyCollectorPeriod"));
        
        logger.addHandler(fh);
        logger.setLevel(Level.INFO);
        
        logger.setLevel(Level.parse((String) RuntimeParams.getParams("LogLvl")));
        
        System.out.println((String) RuntimeParams.getParams("LogLvl"));
        
        
        logger.log(Level.INFO, "finished initiallizing...");
        wasInit = true;
        return true;
    }
  
    
    /**
     *  runs the handler ( this method blocks forever! ).
     */
    public static void run() {
        
        if(!wasInit) return; // check we were initiallized
        int IDs = 0;
        while(true)  {// really want this?
           try {
                logger.log(Level.INFO, "waiting for incoming connection...");
                Socket newSocket = ss.accept();
                logger.log(Level.INFO, "accepted new incoming connection...");
                if(threads.size() == (int) RuntimeParams.getParams("MaxThreads")) {
                    // give the new socket an error and close it
                    logger.log(Level.INFO, "refusing new incoming connection because of overhead");
                    newSocket.close();
                } else {
                    // create a new requestHandler and give it the socket and a new db connection
                    logger.log(Level.INFO, "starting new DB connection...");
                    
                    ClientRequestThread newRequestThread = new ClientRequestThread(ds.getConnection(), newSocket,IDs);
                    threads.add(newRequestThread);
                    logger.log(Level.INFO, "transfering control and starting new client request thread...");
                    newRequestThread.start();
                }
                
                logger.log(Level.INFO, "updating thread arrays");
                updateThreads();                
                logger.log(Level.INFO, threads.size()+" Threads active.");
                logger.getHandlers()[0].flush();
                
           } catch(IOException ex) {
               // something went very wrong
               logger.log(Level.SEVERE, "failed to close socket");
               logger.log(Level.SEVERE, ex.getMessage(), ex);   
           }  catch (SQLException ex) {
               logger.log(Level.SEVERE, "failed to open db connection");
               logger.log(Level.SEVERE, ex.getMessage(), ex);   
               logger.log(Level.SEVERE, "closing...");
               System.exit(1);
           } 
           IDs++;
        }
    }

    /**
     *  updates the thread array to hold only the active threads.
     */
    private synchronized static void updateThreads() {
        for(int i=0; i< threads.size(); i++) {
            if(!threads.get(i).isAlive()) {                
               threads.remove(i);
            }
        }
    }
}
