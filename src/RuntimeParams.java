
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import org.json.JSONObject;

/**
 * FILE : RuntimeParams.java 
 * AUTHORS : Erez Gotlieb
 * DESCRIPTION : This class encaps the access to the run-time parameters of the program
 */
public class RuntimeParams {

    // the map that holds the parameteres 
    private static HashMap<String, Object> params = new HashMap<>();

    static {
        // default params
        params.put("DriverClass", "com.mysql.jdbc.Driver");
        params.put("DBURL", "jdbc:mysql://gweizman.noip.me/main?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull");
        params.put("MaxPoolSize", 10);
        params.put("MaxStatementsPerConnection", 20);
        params.put("MaxIdleTime", 0);
        params.put("RequestPort", 4850);
        params.put("MaxThreads", 5);
        params.put("SocketBackLog", 20);
        params.put("ResponseTimeOut", 2000); // time in millis
        params.put("ResponseWaitInterval",50);
        params.put("LogFileName", "logger.txt"); // time in millis
        params.put("BufferSize", 16384); // time in millis
        params.put("LogLvl","INFO");
        
        params.put("keyLifeTime",1800); // time in sec (30 min)
        params.put("GarbageKeyCollectorPeriod",300000L); // time in millis (5 min)
    }

    /**
     * 
     * @param key the name of the parameter
     * @return the desired parameter, null if no such parameter exists
     */
    public static Object getParams(String key) {
        return params.get(key);
    }

    /**
     * Reads the parameters from a config file
     * 
     * @param fileName name of the parameter file
     * @return true - if file was read, false - otherwise
     * @throws Exception 
     */
    public static boolean readParams(String fileName) throws Exception {
        
        if(fileName == null) {
            return false;
        }
        File f = new File(fileName);
        if(!f.exists()) {
            return false;
        }
      //  params = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        
        JSONObject jsonFormat = new JSONObject(sb.toString().replaceAll("[\\r\\n]", ""));
        String key;
        Iterator<String> keyIterator = jsonFormat.keys();
        while(keyIterator.hasNext()) {
            key = keyIterator.next();                            
            params.put(key, jsonFormat.get(key));
        }
        
        return true;
    }
}
