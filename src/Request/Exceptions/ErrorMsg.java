/**
 * FILE : ErrorMsg.java AUTHORS : Erez Gotlieb DESCRIPTION :
 */
package Request.Exceptions;

import java.util.HashMap;

public class ErrorMsg {

	private static final HashMap<Integer, String> msgs;

	static {
		msgs = new HashMap<>();
		//validation exceptions
		msgs.put(1, "Bad app name");
		msgs.put(2, "Bad app key");
		msgs.put(3, "Bad username");
		msgs.put(4, "Bad user password");
		msgs.put(5, "Permission-group name already exists");
		msgs.put(6, "User has insufficient permissions to perform the desired operations");
		msgs.put(7, "Table name already exists");
		msgs.put(8, "No such table exists");
		msgs.put(9, "No such permission-group exists");
		msgs.put(10, "Must be anon for signup");
		msgs.put(11, "Bad permission group name");
		msgs.put(12, "Bad table name");
		msgs.put(13, "Application must be Master App");
		msgs.put(14, "App name already exists");
		msgs.put(15, "SQL sanitization failed");
		msgs.put(16, "Permission group admin cannot remove his own user permission");
		msgs.put(17, "Cannot add or delete a permission group with a special name");

		msgs.put(18, "Cannot Select from User table using password field in where clause");
		msgs.put(19, "Illegal operand in where clause");
		msgs.put(20, "Cannot assign admin to user not in the permission group");
		msgs.put(21, "New Tables must have a primary key column");
		msgs.put(22, "Permission group for table exist already");
		msgs.put(23, "Cannot remove app admin permissions");
		msgs.put(24, "Cannot delete user that is a permission group admin");
		msgs.put(25, "Cannot add or delete an application with a special name");
		msgs.put(26, "Cannot exist Users user group");
		msgs.put(27, "User already in group");
		msgs.put(28, "Cannot Change Anonymous Details");
		msgs.put(29, "App name is Invalid");
		msgs.put(30, "Cannot publicly select from that table");
		msgs.put(31, "Registration is inactive at the moment");

                msgs.put(32, "Must be User for USER SELECT");
		//execution exceptions
		msgs.put(51, "Must validate before executing");
		msgs.put(52, "General excution error");

		// 
		msgs.put(100, "Bad request format");
		msgs.put(500, "Internal Server Error!");
		msgs.put(501, "Request to long!");
	}

	public static String getErrorMsg(int errorCode) {
		return msgs.get(errorCode);
	}

	public static String getErrorMsg(RequestException ex) {
		return getErrorMsg(ex.getErrorCode());
	}
}
