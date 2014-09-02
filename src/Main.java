
import java.io.IOException;


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
        ThreadHandler.init("");
        ThreadHandler.run();
    }
    
}
