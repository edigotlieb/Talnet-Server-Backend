/**
 * FILE : Request.java AUTHORS : Idan Berkovits
 */
package Request;

import Exceptions.ExecutionException;
import Exceptions.ParsingException;
import Exceptions.ValidationException;
import Request.Preformance.PerformanceSet;
import Request.RequestArgument.RequestArgumentStructure;
import Request.Validation.ValidationSet;
import RequestArgumentAssignment.RequestArgumentStructureAssignment;
import SQL.PreparedStatements.StatementPreparer;
import SQL.SqlExecutor;
import Utilities.Hashing;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * this class represents a type of request that a client could send
 *
 * @author idanb55
 */
public class Request {

	private static Map<String, Map<String, Request>> requestsMap;

	/**
	 * adds requests to the collection of requests
	 *
	 * @param requests a node list of xml elements describing the requests
	 * @throws ParsingException thrown in case of an xml parsing exception
	 */
	public static void addRequests(NodeList requests) throws ParsingException {
		if (requestsMap == null) {
			requestsMap = new HashMap<>();
		}
		for (int i = 0; i < requests.getLength(); i++) {
			Request request = new Request((Element) requests.item(i));
			if (!requestsMap.containsKey(request.getType())) {
				requestsMap.put(request.getType(), new HashMap<String, Request>());
			}
			requestsMap.get(request.getType()).put(request.getName(), request);
			Logger.getGlobal().log(Level.FINE, "Request type:{0} name:{1}", new Object[]{request.getType(), request.getName()});
		}
	}

	/**
	 * retrieves a request from the request collection using a json object
	 * describing the type and name of the request
	 *
	 * @param requestInfo json object describing the type and name of the
	 * request
	 * @return the request
	 */
	public static Request getRequest(JSONObject requestInfo) {
		return requestsMap.get(requestInfo.getString("requestType")).get(requestInfo.getString("requestAction"));
	}
	private String name;
	private String type;
	private RequestArgumentStructure requestArguments;
	private ValidationSet validation;
	private PerformanceSet performance;

	public Request(Element eRequest) throws ParsingException {
		try {
			this.name = eRequest.getAttribute("name"); // the name of the request
			this.type = eRequest.getAttribute("type"); // the type of the request
			// create the structure of request arguments used in the request
			this.requestArguments = new RequestArgumentStructure((Element) eRequest.getElementsByTagName("requestArguments").item(0));
			// creates the set of validations used in the request
			this.validation = new ValidationSet((Element) eRequest.getElementsByTagName("validation").item(0));
			// creates the set of performances used in the request
			this.performance = new PerformanceSet((Element) eRequest.getElementsByTagName("performance").item(0));
		} catch (NumberFormatException ex) {
			throw new ParsingException(ex.getMessage());
		}
	}

	/**
	 * returns the name of the request
	 *
	 * @return the name of the request
	 */
	public String getName() {
		return name;
	}

	/**
	 * the type of the request
	 *
	 * @return the type of the request
	 */
	public String getType() {
		return type;
	}

	/**
	 * this function does the main job of the client request. it validates the
	 * information and authentication given by the user, executes the request
	 * and returns the resultSet to return to the client.
	 *
	 * @param sqlExc the sql executor to use when executing queries in the
	 * execution.
	 * @param challenge the challenge that was sent to the client before the
	 * request sending.
	 * @param requestData a json object with the data to assign int the request
	 * arguments.
	 * @param creds the credentials of the request sent by the client.
	 * @return the resultSet to return to the client.
	 * @throws ValidationException thrown in case where the validation fails
	 * @throws ExecutionException thrown in case where the execution fails
	 * @throws SQLException thrown in case where some sql execution fails (only
	 * when validating)
	 */
	public ResultSet execute(SqlExecutor sqlExc, String challenge, JSONObject requestData, final Credentials creds) throws ValidationException, ExecutionException, SQLException {
		//assigns values to the request arguments
		RequestArgumentStructureAssignment arguments = new RequestArgumentStructureAssignment(this.requestArguments, requestData);

		//authenticate application
		ResultSet app = sqlExc.executePreparedStatement("getAllAppInfoByName", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, creds.getAppName());
			}
		});
		if (!app.next()) {
			throw new ValidationException(1);
		}
		if (!creds.getHashedAppKey().equals(Hashing.MD5Hash(app.getString("APP_KEY") + challenge))) {
			throw new ValidationException(2);
		}

		//authenticate user
		ResultSet user = sqlExc.executePreparedStatement("getUserPermissionGroups", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, creds.getUsername());
			}
		});
		if (!user.next()) {
			throw new ValidationException(3);
		}
		if (!creds.getHashedPassword().equals(Hashing.MD5Hash(app.getString("PASSWORD") + challenge))) {
			throw new ValidationException(4);
		}

		// set the permission collection in the credentials
		List<String> permissions = new ArrayList<>();
		do {
			permissions.add(user.getString("PERMISSION_NAME"));
		} while (user.next());
		creds.setPermissions(permissions);

		try {
			//validate request
			if (!validation.validate(sqlExc, arguments, creds)) {
				throw new ExecutionException(51);
			}
		} catch (ValidationException ex) {
			//if validation failed because "user has insufficient permissions"
			// and the user if super admin the execute eitherway
			if (ex.getErrorCode() != 6 || !creds.isSuperAdmin()) {
				throw ex;
			}
		}
		//perform the request
		return this.performance.preform(sqlExc, arguments, creds);
	}
}
