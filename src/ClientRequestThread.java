
/**
 * FILE : ClientRequestThread.java AUTHORS : Erez Gotlieb
 */
import Request.Credentials;
import Exceptions.ExecutionException;
import Exceptions.RequestException;
import Exceptions.ValidationException;
import Request.Request;
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
import java.io.OutputStreamWriter;

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
	// final String SUCCESS_MSG = "{'ACK':'OldRequest performed'}\n";
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

	@Override
	/**
	 * the run() function of the thread that handles the user request
	 */
	public synchronized void run() {

//<editor-fold defaultstate="collapsed" desc="open streams">
		try {
			Logger.getGlobal().log(Level.INFO, "opening streams...");
			// open a stream            
			this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
			this.out = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream(), "UTF8"));
		} catch (IOException ex) {
			Logger.getGlobal().log(Level.SEVERE, "failed to open streams...", ex);
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
		Logger.getGlobal().log(Level.INFO, "sending challenge to client - {0}", chal);
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="wait for client response">
		try {
			int time = (int) RuntimeParams.getParams("ResponseTimeOut");
			int timeInterval = (int) RuntimeParams.getParams("ResponseWaitInterval");
			Logger.getGlobal().log(Level.INFO, "waiting for client request max {0} millis...", time);
			int i = 0;
			while (i * timeInterval < time && !reader.ready()) {
				Thread.sleep(timeInterval);
			}
			// Thread.sleep(time);
			Logger.getGlobal().log(Level.INFO, "done sleeping...");
			if (!reader.ready()) {
				// client timed-out
				Logger.getGlobal().log(Level.INFO, "client timed out...");
				this.closeThread();
				return;
			}
		} catch (InterruptedException ex) {
			// thread interupted ?
			Logger.getGlobal().log(Level.SEVERE, "InterruptedException...", ex);
			this.closeThread();
			return;
		} catch (IOException ex) {
			Logger.getGlobal().log(Level.SEVERE, "failed to read stream...", ex);
			// cand check if reader is ready
		}
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="proccess client request">
		JSONObject masterObj;
		Credentials creds;
		Request request;
		try {
			int maxLength = (int) RuntimeParams.getParams("BufferSize");
			char[] buffer = new char[maxLength];

			int length = reader.read(buffer);
			if (length == maxLength) {
				// some error
				String errorResponse = this.createErrorResponse(new RequestException(501));
				Logger.getGlobal().log(Level.INFO, "CRT-{0}: RESPONSE: {1}", new Object[]{ID, errorResponse});
				this.out.print(errorResponse);
				this.out.flush();

				this.closeThread();
				return;
			}
			String requestString = new String(buffer);
			requestString = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(requestString);
			Logger.getGlobal().log(Level.INFO, "CRT-{0}: REQUEST: {1}", new Object[]{ID, requestString});

			masterObj = new JSONObject(requestString);
			creds = new Credentials(masterObj.getJSONObject("RequesterCredentials"));
			request = Request.getRequest(masterObj.getJSONObject("RequestInfo"));
			// process response to request
			Logger.getGlobal().log(Level.INFO, "process client request...");
		} catch (IOException ex) {
			// cant read line from stream
			Logger.getGlobal().log(Level.SEVERE, "failed to read request from stream...", ex);
			this.closeThread();
			return;
		} catch (JSONException ex) {
			// bad format! - send bad format error
			Logger.getGlobal().log(Level.WARNING, "JSON parse...", ex);
			String errorResponse = this.createErrorResponse(new RequestException(100));
			Logger.getGlobal().log(Level.INFO, "CRT-{0}: RESPONSE: {1}", new Object[]{ID, errorResponse});
			this.out.print(errorResponse);
			this.out.flush();
			this.closeThread();
			return;
		}
		creds.setIsLocalRequest((socket.getInetAddress().isLoopbackAddress()));
		ResultSet resultSet;
		try {
			// validate request
			resultSet = request.execute(new SqlExecutor(con), chal, masterObj.getJSONObject("RequestData"), creds);
		} catch (SQLException ex) {
			// some bad SQL
			Logger.getGlobal().log(Level.WARNING, "SQL error...", ex);
			this.out.print(this.createErrorResponse(new RequestException(500)));
			this.out.flush();
			this.closeThread();
			return;
		} catch (ValidationException ex) {
			// validation error
			// send back response
			Logger.getGlobal().log(Level.INFO, "client request denied with validation error...", ex);
			String errorResponse = this.createErrorResponse(ex);
			Logger.getGlobal().log(Level.INFO, "CRT-{0}: RESPONSE: {1}", new Object[]{ID, errorResponse});
			this.out.print(errorResponse);
			this.out.flush();
			this.closeThread();
			return;
		} catch (ExecutionException ex) {
			Logger.getGlobal().log(Level.INFO, "client request denied with performance error...", ex);
			String errorResponse = this.createErrorResponse(ex);
			Logger.getGlobal().log(Level.INFO, "CRT-{0}: RESPONSE: {1}", new Object[]{ID, errorResponse});
			this.out.print(errorResponse);
			this.out.flush();
			this.closeThread();
			return;
		} catch (JSONException ex) {
			// bad format! - send bad format error
			Logger.getGlobal().log(Level.INFO, "client request denied with bad format error...", ex);
			String errorResponse = this.createErrorResponse(new RequestException(100));
			Logger.getGlobal().log(Level.INFO, "CRT-{0}: RESPONSE: {1}", new Object[]{ID, errorResponse});
			this.out.print(errorResponse);
			this.out.flush();
			this.closeThread();
			return;
		} catch (Exception ex) {
			// some bad SQL
			Logger.getGlobal().log(Level.WARNING, "General error...", ex);
			this.out.print(this.createErrorResponse(new RequestException(500)));
			this.out.flush();
			this.closeThread();
			return;
		}
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="response for client">
		try {
			// send resultSet                    
			String response = createResponse(resultSet, 1, "");
			Logger.getGlobal().log(Level.FINE, "CRT-{0}: RESPONSE: {1}", new Object[]{ID, response});
			this.out.print(response);
			this.out.flush();
		} catch (SQLException ex) {
			// cant write result or read result set
			Logger.getGlobal().log(Level.WARNING, "cant write result or read result set", ex);
			this.out.print(createErrorResponse(new RequestException(500)));
			this.out.flush();
		}
		Logger.getGlobal().log(Level.INFO, "Response sent to client");
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
					case java.sql.Types.VARCHAR: {
						String result;
						result = rs.getString(column_name);
						obj.put(column_name, stripEdges(JSONObject.quote(result)));
					}
					break;
					case java.sql.Types.TINYINT:
						obj.put(column_name, rs.getInt(column_name));
						break;
					case java.sql.Types.SMALLINT:
						obj.put(column_name, rs.getInt(column_name));
						break;
					case java.sql.Types.DATE: {
						Date d = rs.getDate(column_name);
						obj.put(column_name, d == null ? "" : d.toString());
					}
					break;
					case java.sql.Types.TIMESTAMP: {
						Timestamp d = rs.getTimestamp(column_name);
						obj.put(column_name, d == null ? "" : d.toString());
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
		String data = (rs == null || !rs.next()) ? "[]" : valueOf(rs);
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
	private synchronized String createErrorResponse(Exception ex) {
		return createErrorResponse(ex.getMessage());
	}

	/**
	 * Closes the thread and all of it's assets.
	 */
	private void closeThread() {
		Logger.getGlobal().log(Level.INFO, "closing request thread...");
		try {
			//       this.reader.close();
			//        this.writer.close();
			this.socket.close();
			this.con.close();
			log.getHandlers()[0].flush();
		} catch (IOException | SQLException ex) {
			// couldnt close stuff
			Logger.getGlobal().log(Level.INFO, "failed at closing request thread...", ex);
		}
	}

	private static String stripEdges(String quote) {
		return quote.substring(1, quote.length() - 1);
	}
}
