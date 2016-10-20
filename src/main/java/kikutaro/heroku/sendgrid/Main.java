package kikutaro.heroku.sendgrid;

import java.util.List;
import javax.servlet.MultipartConfigElement;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
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
        
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);

        post("/sendgrid", (req, res) -> {
            System.out.println(req.body());
            System.out.println("リクエストのパース開始");
            List<FileItem> items = upload.parseRequest(req.raw());
            items.stream().forEach(fi -> {
                if(fi.isFormField()) {
                    System.out.println(fi.getFieldName());
                    System.out.println(fi.getString());
                }
            });
            
            System.out.println("メールタイトルは " + req.raw().getPart("subject"));
            System.out.println("メール内容" + req.raw().getPart("text"));
            
            return req.body();
        });
    }
    
}
