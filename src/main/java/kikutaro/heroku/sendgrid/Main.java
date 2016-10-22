package kikutaro.heroku.sendgrid;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import kikutaro.heroku.plotly.PlotlyResult;
import kikutaro.heroku.plotly.PlotlyHelper;
import kikutaro.heroku.sendgrid.model.CsObject;
import kikutaro.heroku.sendgrid.model.Document;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.get;
import static spark.Spark.post;

/**
 *
 * @author kikuta
 */
public class Main {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, UnirestException {        
        port(Integer.valueOf(System.getenv("PORT")));
        
        get("/sendgrid", (req, res) -> "Hello SendGridder!");
        
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);

        post("/sendgrid", (req, res) -> {
            System.out.println(req.body());
            
            String cognitiveKey = System.getenv("MS_CS_TEXT_ANALYTICS_API_KEY");
            if(Strings.isNullOrEmpty(cognitiveKey)) {
                System.out.println("Cognitive Serviceのキーが登録されていません");
            }
            String plotlyUser = System.getenv("PLOTLY_USER_ID");
            if(Strings.isNullOrEmpty(plotlyUser)) {
                System.out.println("plot.lyのユーザIDが登録されていません");
            }
            String plotlyPass = System.getenv("PLOTLY_PASSWORD");
            if(Strings.isNullOrEmpty(plotlyPass)) {
                System.out.println("plot.lyのパスワードが登録されていません");
            }
            
            Gson gson = new Gson();
            
            System.out.println("リクエストのパース開始");
            List<FileItem> items = upload.parseRequest(req.raw());
            items.stream().forEach(fi -> {
                if(fi.isFormField()) {
                    System.out.println(fi.getFieldName());
                    System.out.println(fi.getString());
                    
                    if(StringUtils.equals(fi.getFieldName(), "text")) {
                        CsObject csObj = new CsObject();
                        Document doc = new Document();
                        doc.setId(Calendar.getInstance().toString());
                        doc.setLanguage("en");
                        doc.setText(fi.getString());
                        csObj.setDocuments(Arrays.asList(doc));
                        
                        try {
                            HttpResponse<JsonNode> result = Unirest.post("https://westus.api.cognitive.microsoft.com/text/analytics/v2.0/sentiment")
                                    .header("Content-Type", "application/json")
                                    .header("Ocp-Apim-Subscription-Key", cognitiveKey)
                                    .body(gson.toJson(csObj))
                                    .asJson();
                            System.out.println("あなたの送信したメールのSentimet : " + result.getBody().toString());
                            
                            HttpResponse<JsonNode> plotlyGridRet = Unirest.post("https://api.plot.ly/v2/grids")
                                    .basicAuth(plotlyUser, plotlyPass)
                                    .header("Content-Type", "application/json")
                                    .header("Plotly-Client-Platform", "Java")
                                    .body(PlotlyHelper.gridSentimentData(Double.parseDouble(result.getBody().toString())))
                                    .asJson();
                            
                            PlotlyResult retGrid = gson.fromJson(plotlyGridRet.getBody().toString(), PlotlyResult.class);
                            
                            HttpResponse<JsonNode> plotlyPlotRet = Unirest.post("https://api.plot.ly/v2/plots")
                                    .basicAuth(plotlyUser, plotlyPass)
                                    .header("Content-Type", "application/json")
                                    .header("Plotly-Client-Platform", "Java")
                                    .body(PlotlyHelper.plotPieData(retGrid.getFile().getFid(), retGrid.getFile().getCols().get(0).getUid(), retGrid.getFile().getCols().get(1).getUid()))
                                    .asJson();
                            PlotlyResult retPlot = gson.fromJson(plotlyPlotRet.getBody().toString(), PlotlyResult.class);
                            
                            System.err.println(retPlot.getFile().getEmbed_url());
                            
                        } catch (UnirestException ex) {
                            System.out.println(ex.getMessage());
                        } catch (IOException ex) {
                        }
                    }
                }
            });
            
            return 200;
        });
    }
    
}
