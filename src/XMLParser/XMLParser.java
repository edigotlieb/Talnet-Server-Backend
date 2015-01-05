/**
 * FILE : XMLParser.java AUTHORS : Idan Berkovits
 */
package XMLParser;

import Exceptions.ErrorMsg;
import Exceptions.ParsingException;
import Request.Request;
import Request.RequestArgument.ArgumentType.EnumType;
import SQL.PreparedStatements.PreparedStatementStrings;
import Utilities.Constants;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * this class handles all xml parsing
 *
 * @author idanb55
 */
public class XMLParser {

	/**
	 * parse every xml file in the xml/ directory (recursively)
	 *
	 * @throws ParsingException thrown in case of xml parsing exception
	 */
	public static void parse() throws ParsingException {
		Queue<File> dirs = new LinkedList<>();
		dirs.add(new File("xml/"));
		while (!dirs.isEmpty()) {
			for (File file : dirs.poll().listFiles()) {
				if (file.isDirectory()) {
					dirs.add(file);
				} else if (file.isFile() && file.getName().endsWith(".xml")) {
					parseFile(file);
				}
			}
		}
	}

	/**
	 * parse an xml talnet file
	 *
	 * @param xmlfile the file to parse
	 * @throws ParsingException thrown in case of xml parsing exception
	 */
	public static void parseFile(File xmlfile) throws ParsingException {
		Logger.getGlobal().log(Level.FINE, "Parsing {0}...", xmlfile.getPath());
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlfile);
			doc.getDocumentElement().normalize();
			Element eTalnet = doc.getDocumentElement();
			switch (eTalnet.getAttribute("type")) {
				case "request":
					Request.addRequests(eTalnet.getElementsByTagName("request"));
					break;
				case "argumentEnums":
					EnumType.addEnumTypes(eTalnet.getElementsByTagName("enum"));
					break;
				case "exceptionsMsg":
					ErrorMsg.addErrorMsg(eTalnet.getElementsByTagName("exception"));
					break;
				case "preparedSQL":
					PreparedStatementStrings.addPredaredStatements(eTalnet.getElementsByTagName("preparedSql"));
					break;
				case "constants":
					Constants.addConstants(eTalnet.getElementsByTagName("constant"));
					break;
			}
		} catch (SAXException | IOException | ParserConfigurationException | IllegalArgumentException ex) {
			throw new ParsingException(ex.getMessage(), ex);
		}
		Logger.getGlobal().log(Level.FINE, "{0} completed", xmlfile.getPath());
	}
}
