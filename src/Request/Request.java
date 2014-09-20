package Request;

import Request.Exceptions.ExecutionException;
import Request.Exceptions.ValidationException;
import SQL.PreparedStatements.StatementPreparer;
import SQL.SqlExecutor;
import SQL.Utilities.ExistenceValidator;
import Utilities.Hashing;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author talpiot
 */
public abstract class Request {

	protected final Credentials creds;
	private boolean validated;



    public void setIsLocalRequest(boolean isLocalRequest) {
        this.creds.setIsLocalRequest(isLocalRequest);
    }                
        
	public Request(Credentials creds) {
		this.creds = creds;
		this.validated = false;
	}

	public enum TYPE {
		USER, APP, DTD
	}

	public final ResultSet execute(SqlExecutor sqlExc) throws SQLException,ExecutionException {
		if (!this.validated) {
			throw new ExecutionException(51);
		}
		return performRequest(sqlExc);
	}

	protected abstract ResultSet performRequest(SqlExecutor sqlExc) throws SQLException;

	// abstract public Credentials getCreds();
	abstract public TYPE getType();

	public final boolean Validate(SqlExecutor sqlExc, String challenge) throws SQLException, ValidationException {
		ResultSet rset;

		String app_key = ExistenceValidator.appByName(sqlExc, this.creds.getAppName());
		if (app_key.length() == 0) {
			throw new ValidationException(1);
		}
                
                // System.out.println(Hashing.MD5Hash(app_key + challenge));
               
		if (!this.creds.getHashedAppKey().equals(Hashing.MD5Hash(app_key + challenge))) {
			throw new ValidationException(2);
		}

		final String username = this.creds.getUsername();
		rset = sqlExc.executePreparedStatement("getUserPermissionGroups", new StatementPreparer() {
			@Override
			public void prepareStatement(PreparedStatement ps) throws SQLException {
				ps.setString(1, username);
			}
		});                
		if (!rset.next()) {
			throw new ValidationException(3);
		}
		String hashed_pass = rset.getString("PASSWORD");

		if (!this.creds.getHashedPassword().equals(Hashing.MD5Hash(hashed_pass + challenge))) {
			throw new ValidationException(4);
		}
		this.creds.setMoreInfo(rset.getString("FIRST_NAME") + rset.getString("LAST_NAME"),
				rset.getString("DISPLAY_NAME"),
				rset.getString("EMAIL"),
				rset.getInt("YEAR"),
				rset.getInt("ROOM_NUM"));
		List<String> permissions = new ArrayList<>();
		do {
			permissions.add(rset.getString("PERMISSION_NAME"));
		} while (rset.next());

		this.creds.setPermissions(permissions);

		try {
			this.validated = CheckPermissions(sqlExc);
			return this.validated;
		} catch (ValidationException ex) {
			if (ex.getErrorCode() != 6 || !this.creds.isSuperAdmin()) {
				throw ex;
			}
		        // this.validated = this.creds.isSuperAdmin();
			return (this.validated = true);
		}

	}

	protected abstract boolean CheckPermissions(SqlExecutor sqlExc) throws SQLException, ValidationException;
}
