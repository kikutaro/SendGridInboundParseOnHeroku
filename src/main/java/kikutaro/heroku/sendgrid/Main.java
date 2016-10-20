package kikutaro.heroku.sendgrid;

import javax.servlet.MultipartConfigElement;
import spark.Request;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

/**
 *
 * @author kikuta
 */
public class Main {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        port(Integer.valueOf(System.getenv("PORT")));
        
        get("/sendgrid", (req, res) -> "Hello SendGridder!");

        post("/sendgrid", (req, res) -> {
            System.out.println(req.body());
            
            MultipartConfigElement multipartConfigElement = new MultipartConfigElement("/");
            req.attribute(org.eclipse.jetty.server.Request.__MULTIPART_CONFIG_ELEMENT ,multipartConfigElement);
            
            System.out.println("メールタイトルは " + req.raw().getPart("subject"));
            System.out.println("メール内容" + req.raw().getPart("text"));
            
            return req.body();
        });
    }
    
}
