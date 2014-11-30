/**
 * FILE : Credentials.java AUTHORS : Erez Gotlieb
 */
package Request;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.json.JSONObject;

/**
 * this class handles the credentials part of a client request meaning it
 * handles the authenticated user and application
 *
 * @author idanb55
 */
public class Credentials {

	public final static String superAdmin = "Super_Admin";
	public final static String appAdminSuffix = "admin";
	public final static String developer = "Developer";
	public final static String anonymous = "Anonymous";
	public final static String masterAppName = "talnet";
	public final static String userPermission = "User";
	private final String username, hashedPassword;
	private final String appName, hashedAppKey;
	private List<String> permissions;
	private boolean isLocalRequest = false;

	/**
	 * constructs a Credentials instance using the json object given by the
	 * client
	 *
	 * @param jsonCreds the json object given by the client
	 */
	public Credentials(JSONObject jsonCreds) {
		this(jsonCreds.getString("username"), jsonCreds.getString("password"),
				jsonCreds.getString("appName"), jsonCreds.getString("appKey"));
	}

	/**
	 * constructs a Credetials instance with user and app details
	 *
	 * @param username the authenticated user name
	 * @param hashed_password the authenticated user md5 hashed password
	 * @param app_name the authenticated application name
	 * @param hashed_app_key the authenticated application md5 hashed key
	 */
	protected Credentials(String username, String hashed_password, String app_name, String hashed_app_key) {
		this.username = username;
		this.hashedPassword = hashed_password;
		this.appName = app_name;
		this.hashedAppKey = hashed_app_key;
	}
	
	public String getValue(String src){
		src = src.replace("{creds:username}", this.getUsername());
		src = src.replace("{creds:appname}", this.getAppName());
		return src;
	}

	/**
	 * set the local request member
	 *
	 * @param isLocalRequest whether the request was local or not
	 */
	public void setIsLocalRequest(boolean isLocalRequest) {
		this.isLocalRequest = isLocalRequest;
	}

	/**
	 * whether this user is a member of Super_Admin group
	 *
	 * @return true if this user is a member of Super_Admin group, otherwise,
	 * false
	 */
	public boolean isSuperAdmin() {
		return isInPermissionGroup(superAdmin);
	}

	/**
	 * returns whether the application is talnet and it is a local request
	 * (basically if it is safe to trust this client and recieve plain
	 * passwords)
	 *
	 * @return true if the application is talnet and it is a local request,
	 * otherwise, false
	 */
	public boolean isMasterApplication() {
		return this.appName.equals(masterAppName) && isLocalRequest;
	}

	/**
	 * returns whether the authenticated user is a Super_Admin or an admin of
	 * the authenticated app (basically if it can skip a "User has insufficient
	 * permissions" exception
	 *
	 * @param app the app to check for
	 * @return true if the authenticated user is a Super_Admin or an admin of
	 * the authenticated app, otherwise, false
	 */
	public boolean isAppSuperAdmin(String app) {
		return isSuperAdmin() || isInPermissionGroup(app + "_" + appAdminSuffix);
	}

	/**
	 * whether this user is a member of Developer group
	 *
	 * @return true if this user is a member of Developer group, otherwise,
	 * false
	 */
	public boolean isDeveloper() {
		return isSuperAdmin() || isInPermissionGroup(Credentials.developer);
	}

	/**
	 * whether this user is a member of a given group
	 *
	 * @param permissionGroupName the group to check
	 * @return true if this user is a member of a given group, otherwise, false
	 */
	public boolean isInPermissionGroup(String permissionGroupName) {
		if (permissionGroupName.equals(anonymous)) {
			return true;
		}
		return this.permissions.contains(permissionGroupName);
	}

	/**
	 * sets the collection of permissions of this user
	 *
	 * @param permissions the collection of permissions of this user to set
	 */
	public void setPermissions(Collection<String> permissions) {
		this.permissions = new ArrayList<>(permissions);
	}

	/**
	 * returns the authenticated user name
	 *
	 * @return the authenticated user name
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * returns the authenticated user md5 hashed password
	 *
	 * @return the authenticated user md5 hashed password
	 */
	public String getHashedPassword() {
		return this.hashedPassword;
	}

	/**
	 * returns the authenticated application name
	 *
	 * @return the authenticated application name
	 */
	public String getHashedAppKey() {
		return this.hashedAppKey;
	}

	/**
	 * returns the authenticated application md5 hashed key
	 *
	 * @return the authenticated application md5 hashed key
	 */
	public String getAppName() {
		return this.appName;
	}
}
