
import Request.Exceptions.ErrorMsg;
import Request.Exceptions.ExecutionException;
import Request.Exceptions.ValidationException;
import Request.Request;
import Request.RequestFactory;
import SQL.SqlExecutor;
import Utilities.Hashing;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import SQL.Utilities.Utils;

/**
 * This class handles a user request from start to finish.
 */
public class ClientRequestThread extends Thread {

    // the db connection
    Connection con;
    
    // the tcp socket
    Socket socket;
    
    // the logger being used
    static final Logger log = Logger.getGlobal();
    
    // the socket reader
    BufferedReader reader;    
    
    // the socket printer
    PrintWriter out;
    
    // the thread ID
    private final int ID;
    
    // the LOG format
    private final static String LOG_FORMAT_MSG = "CRT-%d: %s";

    // response format
    final static String RESPONSE_FORMAT = "{\"Status\":\"%d\" , \"Message\":\"%s\", \"Data\":%s}";
    
    // final String ERROR_FORMAT = "{'ERROR':'%s'}";
    // final String SUCCESS_MSG = "{'ACK':'Request performed'}\n";

    /**
     * 
     * @param con the db connection
     * @param socket the tcp socket
     * @param ID the thread ID
     */
    public ClientRequestThread(Connection con, Socket socket, int ID) {
        this.con = con;
        this.socket = socket;
        this.ID = ID;
    }

    /**
     * logs a string message
     * 
     * @param msg the message to log
     * @param lvl the log level
     */
    public void logMSG(String msg, Level lvl) {
        log.log(lvl, String.format(LOG_FORMAT_MSG, ID, msg));
    }

    /**
     * logs an exception.
     * 
     * @param ex the exception to log
     * @param lvl the log level
     */
    public void logException(Exception ex, Level lvl) {
        log.log(lvl, String.format(LOG_FORMAT_MSG, ID, ex.getMessage()), ex);
    }

    
    @Override
    /**
     *  the run() function of the thread that handles the user request
     */
    public synchronized void run() {

//<editor-fold defaultstate="collapsed" desc="open streams">
        try {
            logMSG("opening streams...", Level.INFO);
            // open a stream
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));            
            this.out = new PrintWriter(this.socket.getOutputStream());
        } catch (IOException ex) {
            logMSG("failed to open streams...", Level.INFO);
            logException(ex, Level.INFO);
            this.closeThread();
            return;
        }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="send challenge">
        String chal;

        // send a challenge
        chal = Hashing.generateChallenge();
        this.out.println(chal);
        this.out.flush();
        logMSG(String.format("sending challenge to client - %s", chal), Level.INFO);

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="wait for client response">
        try {
            int time = (int) RuntimeParams.getParams("ResponseTimeOut");
            int timeInterval = (int) RuntimeParams.getParams("ResponseWaitInterval");
            logMSG(String.format("waiting for client request max %d millis...", time), Level.INFO);
            int i = 0;
            while(i*timeInterval < time && !reader.ready()) {
                Thread.sleep(timeInterval);
            }
            // Thread.sleep(time);
            logMSG("done sleeping...", Level.INFO);
            if (!reader.ready()) {
                // client timed-out
                logMSG("client timed out...", Level.INFO);
                this.closeThread();
                return;
            }

        } catch (InterruptedException ex) {
            // thread interupted ?
            logMSG("InterruptedException...", Level.INFO);
            logException(ex, Level.INFO);
            this.closeThread();
            return;
        } catch (IOException ex) {
            logMSG("failed to read stream...", Level.INFO);
            logException(ex, Level.INFO);
            // cand check if reader is ready
        }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="proccess client request">
        Request clientRequest;
        try {
            int maxLength = (int) RuntimeParams.getParams("BufferSize");
            char[] buffer = new char[maxLength];
            int length = reader.read(buffer);
            if (length == maxLength) {
                // some error
                this.out.print(this.createErrorResponse(501));
                this.out.flush();

                this.closeThread();
                return;
            }
            // logMSG("REQUEST: "+(new String(buffer)),Level.INFO);
            // process response to request
            clientRequest = RequestFactory.createRequestFromString(new String(buffer));
            if(clientRequest == null) {
                throw new JSONException("RequestFactory Fail...");
            }
            this.logMSG("proc client request...", Level.INFO);
        } catch (IOException ex) {
            // cant read line from stream
            logMSG("failed to read request from stream...", Level.INFO);
            logException(ex, Level.INFO);
            this.closeThread();
            return;
        } catch (JSONException ex) {
            // bad format! - send bad format error
            this.out.print(this.createErrorResponse(100));
            this.out.flush();
            logMSG("request format error...", Level.INFO);
            logException(ex, Level.INFO);
            this.closeThread();
            return;

        }
        clientRequest.setIsLocalRequest((socket.getInetAddress().isLoopbackAddress()));
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="validate client request">
        try {
            // validate request
            clientRequest.Validate(new SqlExecutor(con), chal);
        } catch (SQLException ex) {
            // some bad SQL
            logMSG("validation SQL error...", Level.SEVERE);
            logException(ex, Level.SEVERE);

            this.out.print(this.createErrorResponse(500));
            this.out.flush();

            this.closeThread();
            return;
        } catch (ValidationException ex) {
            // validation error
            // send back response
            logMSG("client request denied with error code " + ex.getErrorCode(), Level.INFO);
                // logException(ex, Level.INFO);

            // System.out.print(this.createErrorResponse(ex.getErrorCode()));                
            this.out.print(this.createErrorResponse(ex.getErrorCode()));
            this.out.flush();

            this.closeThread();
            return;
        }
        logMSG("Client request validated", Level.INFO);
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="exe client request">
        ResultSet resultSet = null;
        try {
            resultSet = clientRequest.execute(new SqlExecutor(con));
            // execute request            
        } catch (SQLException ex) {
            // execution went wrong
            this.logMSG("general execution error...", Level.INFO);
            this.logException(ex, Level.INFO);
            this.out.printf(this.createErrorResponse(52));
            this.out.flush();
            this.closeThread();
            return;

        } catch (ExecutionException ex) {
            // not validated
            this.logMSG(ErrorMsg.getErrorMsg(51), Level.INFO);
            return;
        } catch (Exception ex) {
            
            this.out.print(this.createErrorResponse(500));
            this.out.flush();
            
            ex.printStackTrace();
            // something else went wrong
            this.closeThread();
            return;
        }
        logMSG("Client request executed", Level.INFO);
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="response for client">
        try {
            // send resultSet                    
                //this.writer.write(createResponse(resultSet));
                String response = createResponse(resultSet, 1, "");
               //  System.out.println(response);
                this.out.print(response);
            
            //this.writer.flush();
            this.out.flush();

        } catch (SQLException ex) {
            // cant write result or read result set
             logMSG("cant write result or read result set", Level.INFO);
             this.out.print(createErrorResponse(500));
             this.out.flush();
        }
        logMSG("Response sent to client", Level.INFO);
        this.closeThread();
//</editor-fold>

    }

    /**
     * This method converts a resultSet into a JSON data value
     * 
     * @param rs the result set to convert
     * @return the JSON format string
     * @throws SQLException if the result set throws it
     */
    private synchronized String valueOf(ResultSet rs) throws SQLException {
        JSONArray json = new JSONArray();
        ResultSetMetaData rsmd = rs.getMetaData();
        rs.beforeFirst();        
        while (rs.next()) {
            int numColumns = rsmd.getColumnCount();
            JSONObject obj = new JSONObject();

            for (int i = 1; i < numColumns + 1; i++) {

                String column_name = rsmd.getColumnLabel(i);  //Bugfix , works better than getColumnName() /Aries 

                switch (rsmd.getColumnType(i)) {
                    case java.sql.Types.ARRAY:
                        obj.put(column_name, rs.getArray(column_name));
                        break;
                    case java.sql.Types.BIGINT:
                        obj.put(column_name, rs.getInt(column_name));
                        break;
                    case java.sql.Types.BOOLEAN:
                        obj.put(column_name, rs.getBoolean(column_name));
                        break;
                    case java.sql.Types.BLOB:
                        obj.put(column_name, rs.getBlob(column_name));
                        break;
                    case java.sql.Types.DOUBLE:
                        obj.put(column_name, rs.getDouble(column_name));
                        break;
                    case java.sql.Types.FLOAT:
                        obj.put(column_name, rs.getFloat(column_name));
                        break;
                    case java.sql.Types.INTEGER:
                        obj.put(column_name, rs.getInt(column_name));
                        break;
                    case java.sql.Types.NVARCHAR:
                        obj.put(column_name, stripEdges(JSONObject.quote(rs.getNString(column_name))));
                        break;
                    case java.sql.Types.VARCHAR:
                        obj.put(column_name, stripEdges(JSONObject.quote(rs.getString(column_name))));
                        break;
                    case java.sql.Types.TINYINT:
                        obj.put(column_name, rs.getInt(column_name));
                        break;
                    case java.sql.Types.SMALLINT:
                        obj.put(column_name, rs.getInt(column_name));
                        break;
                    case java.sql.Types.DATE:    {
                       // obj.put(column_name, rs.getString(column_name));
                        Date d = rs.getDate(column_name);                        
                        obj.put(column_name, d==null ? "": d.toString());
                    }                                             
                        break;
                    case java.sql.Types.TIMESTAMP:  {
                       // obj.put(column_name, rs.getString(column_name));
                        Timestamp d = rs.getTimestamp(column_name);                        
                        obj.put(column_name, d==null ? "": d.toString());
                    }                        
                        break;
                    default:
                        obj.put(column_name, rs.getObject(column_name));
                        break;
                }
            }

            json.put(obj);
        }

        return json.toString();
    }

    /**
     * Create the final response string to be sent back to the user,
     * 
     * @param rs the data result set
     * @param stat the handle status (0 - fail, 1 - success)
     * @param msg the message to append to the responce
     * @return the string to send to the user (raw)
     * @throws SQLException if the ResultSet throws it
     */
    private synchronized String createResponse(ResultSet rs, int stat, String msg) throws SQLException {
        String message = (stat == 0) ? msg : "";
        String data = (rs == null || !rs.next()) ? "[]":valueOf(rs);
        return String.format(RESPONSE_FORMAT, stat, message, data);
    }

    /**
     * Creates a generic error response with the given message
     * 
     * @param msg the error message 
     * @return the string to send to the user (raw)
     */
    private synchronized String createErrorResponse(String msg) {
        return String.format(RESPONSE_FORMAT, 0, msg, "[]");
    }

    /**
     * Creates a error response with a given errorCode as to the ErrorMsg class
     * 
     * @param errorCode the message error code (as defined by ErrorMsg)
     * @return the string to send to the user (raw)
     */
    private synchronized String createErrorResponse(int errorCode) {
        return createErrorResponse(ErrorMsg.getErrorMsg(errorCode));
    }

    /**
     * Closes the thread and all of it's assets.
     */
    private void closeThread() {
        logMSG("closing request thread...", Level.INFO);
        try {
            //       this.reader.close();
            //        this.writer.close();
            this.socket.close();
            this.con.close();
            log.getHandlers()[0].flush();
        } catch (IOException | SQLException ex) {
            // couldnt close stuff
            logMSG("failed at closing request thread...", Level.INFO);
            logException(ex, Level.INFO);
        }
    }

	private static String stripEdges(String quote) {
		return quote.substring(1, quote.length()-1);
	}

}
