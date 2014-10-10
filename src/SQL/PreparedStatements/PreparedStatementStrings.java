/**
 * FILE : PreparedStatementStringas.java AUTHORS : Erez Gotlieb
 */
package SQL.PreparedStatements;

import Exceptions.ParsingException;
import java.util.HashMap;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class PreparedStatementStrings {
	public static void addPredaredStatements(NodeList preparedStatements) throws ParsingException {
		for (int i = 0; i < preparedStatements.getLength(); i++) {
			Element ePreparedStatement = (Element) preparedStatements.item(i);
			String query = ePreparedStatement.getElementsByTagName("query").item(0).getTextContent();
			preparedSql.put(ePreparedStatement.getAttribute("name"), query);
		}
	}
		private static final HashMap<String, String> preparedSql = new HashMap<>();

	public static String getSQL(String key) {
		return preparedSql.get(key);
	}
}
