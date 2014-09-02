/**
 * FILE : Permission.java
 * AUTHORS : Erez Gotlieb
 * DESCRIPTION :
 */
package Request.AppRequest;

public class Permission {

    public enum PERMISSION_TYPE {
        SELECT, INSERT, UPDATE, DELETE
    }

    private PERMISSION_TYPE type;
    private String appName, tableName, permissionGroup;

	public PERMISSION_TYPE getType() {
		return type;
	}

	public String getAppName() {
		return appName;
	}

	public String getTableName() {
		return tableName;
	}

	public String getPermissionGroup() {
		return permissionGroup;
	}

    public Permission(PERMISSION_TYPE type, String appName, String tableName, String permissionGroup) {
        this.type = type;
        this.appName = appName;
        this.tableName = tableName;
        this.permissionGroup = permissionGroup;
    }
}
