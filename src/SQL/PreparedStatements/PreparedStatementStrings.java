/**
 * FILE : PreparedStatementStringas.java AUTHORS : Erez Gotlieb DESCRIPTION :
 */
package SQL.PreparedStatements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PreparedStatementStrings {

	private static final HashMap<String, String> fileNames = new HashMap<>();

	static {
		/*
		 * params: name
		 * cols: value
		 * descriptions: Gives the value of a given param name
		 */
		fileNames.put("getBeckendParamValue", "getBeckendParamValue.sql");
		
		/*
		 * params: Username
		 * cols: USER_ID, USERNAME, PASSWORD (hashed), Name, EMAIL, DATE_REGISTERED, YEAR, DISPLAY_NAME, ROOM_NUM
		 * descriptions: Gives the information of a given user by username
		 */
		fileNames.put("getAllUserInfoByUsername", "select/getAllUserInfoByUsername.sql");
		/*
		 * params: Username
		 * cols: USER_ID, USERNAME, Name, EMAIL, DATE_REGISTERED, YEAR, DISPLAY_NAME, ROOM_NUM
		 * descriptions: Gives the information of a given user by username
		 */
		fileNames.put("getAllUserInfoByUsernameNoPass", "select/getAllUserInfoByUsernameNoPass.sql");
		/*
		 * params: App Name
		 * cols: APP_ID, APP_NAME, APP_KEY
		 * descriptions: Gives the information of a given app by name
		 */
		fileNames.put("getAllAppInfoByName", "select/getAllAppInfoByName.sql");
		/*
		 * params: App Key
		 * cols: APP_ID, APP_NAME, APP_KEY
		 * descriptions: Gives the information of a given app by key
		 */
		fileNames.put("getAllAppInfoByKey", "select/getAllAppInfoByKey.sql");
		/*
		 * params: Username
		 * cols: USER_ID, USERNAME, PASSWORD (hashed), NAME, EMAIL, DATE_REGISTERED, YEAR, DISPLAY_NAME, ROOM_NUM, PERMISSIONGROUP_IF, PERMISSION_NAME, PERMISSIONGROUP_DESCTIPTION, PERMISION_GROUP_ADMIN
		 * descriptions: returns a set of rows, each describes the user info and the info of one of the
		 * permission groups he is in.
		 */
		fileNames.put("getUserPermissionGroups", "select/getUserPermissionGroups.sql");
		/*
		 * params: Username
		 * cols: USERNAME, NAME, EMAIL, DATE_REGISTERED, YEAR, DISPLAY_NAME, ROOM_NUM, PERMISSIONGROUP_IF, PERMISSION_NAME, PERMISSIONGROUP_DESCTIPTION, PERMISION_GROUP_ADMIN
		 * descriptions: returns a set of rows, each describes the user info and the info of one of the
		 * permission groups he is in.
		 */
		fileNames.put("getUserPermissionGroupsNoPass", "select/getUserPermissionGroupsNoPass.sql");
		/*
		 * params: Username, App Name
		 * cols: TABLE_NAME, PERMISSION_TYPE
		 * descriptions: returns all DT permissions a user has in a given app
		 */
		fileNames.put("getUserPermissionsToTables", "select/getUserPermissionsToTables.sql");
		/*
		 * params: Username, App Name, Table name
		 * cols: PERMISSION_TYPE
		 * descriptions: returns all DT permissions a user has in a given app to
		 * a given table
		 */
		fileNames.put("getUserTablePermission", "select/getUserTablePermission.sql");
		/*
		 * params: Table name
		 * cols: TABLE_NAME, APP_NAME, APP_KEY
		 * descriptions: returns the app a table belongs to
		 */
		fileNames.put("getTableInfoByName", "select/getTableInfoByName.sql");
		/*
		 * params: Permission group name
		 * cols: PERMISSIONGROUP_IF, PERMISSION_NAME, PERMISSIONGROUP_DESCTIPTION, PERMISION_GROUP_ADMIN, USERNAME
		 * descriptions: returns the permissiongroup info including the username of the permissiongroup admin
		 */
		fileNames.put("getPermissionGroupInfoByName", "select/getPermissionGroupInfoByName.sql");
		/*
		 * params: app name
		 * cols: TABLE_NAME
		 * descriptions: returns the set of a given app's dynamic table names
		 */
		fileNames.put("getPermissionGroupInfoByName", "select/getPermissionGroupInfoByName.sql");
		/*
		 * params: permission name
		 * cols: USER_ID, USERNAME, PASSWORD (hashed), NAME, EMAIL, DATE_REGISTERED, YEAR, DISPLAY_NAME, ROOM_NUM
		 * descriptions: returns all users with a specified group
		 */
		fileNames.put("getUserInfoWithPermissionGroup", "select/getUserInfoWithPermissionGroup.sql");
		/*
		 * params: app name
		 * descriptions: returns all table names of a given app name
		 */
		fileNames.put("getAppTables", "select/getAppTables.sql");
		/*
		 * params: user name, permission name
		 * descriptions: returns the relation record between a user and a permission if exists
		 */
		fileNames.put("getUserPermissionRelation", "select/getUserPermissionRelation.sql");
		/*
		 * params: group name, table name
		 * descriptions: returns all permissions a group has to the given table
		 */
		fileNames.put("getGroupTablePermission", "select/getGroupTablePermission.sql");
		/*
		 * params: 
		 * descriptions: returns all apps created
		 */
		fileNames.put("getAllApps", "select/getAllApps.sql");
		/*
		 * params: username
		 * descriptions: returns all apps whom a given user is their app admin
		 */
		fileNames.put("getAllAppsUser", "select/getAllAppsUser.sql");
		/*
		 * params: username
		 * descriptions: returns all apps whom a given user is their app admin
		 */
		fileNames.put("getUserPermissionGroupsAdmin", "select/getUserPermissionGroupsAdmin.sql");
/*
		 * params: none
		 * descriptions: returns all permission groups
		 */
		fileNames.put("getAllPermissionGroupsAdmin", "select/getAllPermissionGroupsAdmin.sql");


		/*
		 * params: app name
		 * description: deletes the app record from the app table
		 */
		fileNames.put("DeleteApp", "delete/DeleteApp.sql");

		/*
		 * params: app name
		 * description: deletes all app permissions of a given app
		 */
		fileNames.put("DeleteAppPermissionsByAppName", "delete/DeleteAppPermissionsByAppName.sql");
		/*
		 * params: permission name
		 * description: deletes all app permissions of a given permission group
		 */
		fileNames.put("DeleteAppPermissionsByPermissionName", "delete/DeleteAppPermissionsByPermissionName.sql");
		/*
		 * params: table name
		 * description: deletes all app permissions of a given permission group
		 */
		fileNames.put("DeleteAppPermissionsByTableName", "delete/DeleteAppPermissionsByTableName.sql");
		/*
		 * params: table name
		 * description: deletes all dynamic table records of a given application
		 */
		fileNames.put("DeleteAppTables", "delete/DeleteAppTables.sql");
		/*
		 * params: permission name
		 * description: deletes a permission group record
		 */
		fileNames.put("DeletePermission", "delete/DeletePermission.sql");
		/*
		 * params: permission name
		 * description: deletes all relation records between a user and the given permission
		 */
		fileNames.put("DeletePermissionUsers", "delete/DeletePermissionUsers.sql");
		/*
		 * params: table name, permission name, permission type
		 * description: deletes a specific app premission with the given info
		 */
		fileNames.put("DeleteSingleAppPermission", "delete/DeleteSingleAppPermission.sql");
		/*
		 * params: table name
		 * description: deletes a given table record from the dynamic table table
		 */
		fileNames.put("DeleteTable", "delete/DeleteTable.sql");
		/*
		 * params: permission name, username
		 * description: removes a relation between a given username and a given permission name
		 */
		fileNames.put("RemoveUserPermission", "delete/RemoveUserPermission.sql");
		/*
		 * params: username
		 * description: deletes user record from users table
		 */
		fileNames.put("DeleteUser", "delete/DeleteUser.sql");
		/*
		 * params: username
		 * description: deletes user permissions with a given user name
		 */
		fileNames.put("DeletePermissionUsersByUser", "delete/DeletePermissionUsersByUser.sql");


		/*
		 * params: user name, permission name
		 * description: sets the given permission admin to be the given user
		 */
		fileNames.put("SetPermissionGroupAdmin", "update/SetPermissionGroupAdmin.sql");
		/*
		 * params: name, display name, email, room, year, username
		 * description: updates information to a given user (last param)
		 */
		fileNames.put("UpdateUser", "update/UpdateUser.sql");
		/*
		 * params: new pass, username
		 * description: sets a new password to a given user (last param)
		 */
		fileNames.put("UpdateUserPassword", "update/UpdateUserPassword.sql");



		/*
		 * params: app name, hashed app key
		 * description: inserts a new application record
		 */
		fileNames.put("AddApp", "insert/AddApp.sql");
		/*
		 * params: permission name, permission desc, admin username
		 * description: inserts a new permission group record
		 */
		fileNames.put("AddPermissionGroup", "insert/AddPermissionGroup.sql");
		/*
		 * params: app name, table name, permission type, permission group id
		 * description: inserts a new applicatoi permission group record
		 */
		fileNames.put("AddPermissionGroupForTable", "insert/AddPermissionGroupForTable.sql");
		/*
		 * params: table name, app name
		 * description: inserts a new dynamic table record
		 */
		fileNames.put("AddTable", "insert/AddTable.sql");
		/*
		 * params: username, permission name
		 * description: inserts a user permission relation record
		 */
		fileNames.put("AddUserPermissionGroup", "insert/AddUserPermissionGroup.sql");
		/*
		 * params: user name, hashed password, name, display name, email, room, year
		 * description: inserts a new user record
		 */
		fileNames.put("AddUser", "insert/AddUser.sql");
                /*
                 * params: Table Name
		 * description: gets the permission groups of the specific dynamic table
                 */
                fileNames.put("getTablePermissions", "select/getTablePermissions.sql");
	}
	private final HashMap<String, String> PreparedSql = new HashMap<>();
	private static PreparedStatementStrings instance = null;

	public static PreparedStatementStrings getInstance() {
		if (instance != null) {
			return instance;
		} else {
			return (instance = new PreparedStatementStrings());
		}
	}

	private PreparedStatementStrings() {
		readFiles();
	}

	private void readFiles() {
		for (String key : fileNames.keySet()) {
			try {
				PreparedSql.put(key, new String(Files.readAllBytes(Paths.get("preparedSQL/" + fileNames.get(key)))));
			} catch (IOException ex) {
				Logger.getLogger(PreparedStatementStrings.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	public String getSQL(String key) {
		return this.PreparedSql.get(key);
	}
}
