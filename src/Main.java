/**
 * FILE : Main.java AUTHORS : Erez Gotlieb & Idan Berkovits
 */
import Exceptions.ParsingException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * the main class, runs the backend
 *
 * @author Administrator
 */
public class Main {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws IOException {
		System.setProperty("file.encoding", "UTF8");

		//initialize logger
		FileHandler fh = new FileHandler("log/" + new java.text.SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date().getTime()) + ".txt");
		fh.setFormatter(new SimpleFormatter());
		Logger.getGlobal().addHandler(fh);
		//logger.addHandler(new StreamHandler(System.out, new SimpleFormatter()));
		ThreadHandler.setLogLevel(Logger.getGlobal(), Level.INFO);

		Logger.getGlobal().log(Level.INFO, "file.encoding={0}", System.getProperty("file.encoding"));
		Logger.getGlobal().log(Level.INFO, "Default Charset={0}", Charset.defaultCharset());

		//ThreadHandler.init(args[0]);
		try {
			Logger.getGlobal().log(Level.INFO, "XML parsing...");
			XMLParser.XMLParser.parse();
			Logger.getGlobal().log(Level.INFO, "XML parsing completed");
		} catch (ParsingException ex) {
			Logger.getGlobal().log(Level.SEVERE, "XML parsing failed! Exiting...", ex);
			return;
		}
		ThreadHandler.run();
	}
}
