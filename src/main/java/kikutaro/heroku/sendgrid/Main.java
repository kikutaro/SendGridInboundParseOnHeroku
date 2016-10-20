package kikutaro.heroku.sendgrid;

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

        post("/sendgrid", (req, res) -> req.body());
    }
    
}
