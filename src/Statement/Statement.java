/**
 * FILE : Statement.java AUTHORS : Erez Gotlieb
 */
package Statement;

import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class defines an abstract SQL WHERE statement allowed in our system
 */
abstract public class Statement {

	/**
	 * checks whether a specific column is used in the where clause
	 *
	 * @param colname the column name to check
	 * @return true if colname is being used, otherwise, false
	 */
	abstract public boolean isColumnIn(String colname);

	/**
	 * validates the legality of the operands in the Statement
	 *
	 * @return true if all operands are legal, otherwise, false
	 */
	abstract public boolean validateOperands();

	@Override
	abstract public String toString();

	/**
	 * creates a new Statement using client given data in json object form
	 *
	 * @param requestData
	 * @return
	 */
	public static Statement statementFactory(JSONObject requestData) {
		if (requestData.length() == 0) {
			return new EmptyStatement();
		}
		if (requestData.length() > 1) {
			throw new JSONException("Bad Format");
		}
		JSONObject termData;
		if (requestData.has("Term")) {
			termData = requestData.getJSONObject("Term");
			if (termData.get("Value") instanceof JSONArray) {
				Logger.getGlobal().log(Level.INFO, "DEBUG JSONArray");
			} else {
				Logger.getGlobal().log(Level.INFO, "DEBUG Object");
			}
			return new BaseStatement(termData.getString("Field"), termData.get("Value"), termData.getString("Op"));
		} else if (requestData.has("AND")) {
			termData = requestData.getJSONObject("AND");
			return new AndStatement(statementFactory(termData.getJSONObject("firstStatement")), statementFactory(termData.getJSONObject("secondStatement")));
		} else if (requestData.has("OR")) {
			termData = requestData.getJSONObject("OR");
			return new OrStatement(statementFactory(termData.getJSONObject("firstStatement")), statementFactory(termData.getJSONObject("secondStatement")));
		} else {
			throw new JSONException("Bad Format");
		}
	}
}
