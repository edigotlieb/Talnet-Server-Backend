
import java.io.IOException;
import java.nio.charset.Charset;


/**
 *
 * @author Administrator
 */
public class Main {

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
     //   String source = "{ \"RequesterCredentials\": { \"appName\":\"testApp\" , \"appKey\":\"testKey\" , \"username\":\"testUsername\", \"password\":\"testPassword\" } , \"RequestInfo\": { \"requestType\":\"USER\" , \"requestAction\":\"SIGN_IN\" } , \"RequestData\": {} }";
     //   Request req = RequestFactory.createRequestFromString(source);
    //    System.out.println(req.getType().toString());
        System.out.println("Default Charset=" + Charset.defaultCharset());
    	System.setProperty("file.encoding", "UTF-8");
    	System.out.println("file.encoding=" + System.getProperty("file.encoding"));
    	System.out.println("Default Charset=" + Charset.defaultCharset());    	      
        
        ThreadHandler.init(args[0]);
        ThreadHandler.run();
    }
    
}
