package kikutaro.heroku.sendgrid;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import kikutaro.heroku.plotly.PlotlyResult;
import kikutaro.heroku.plotly.PlotlyHelper;
import kikutaro.heroku.sendgrid.model.SentimentRequest;
import kikutaro.heroku.sendgrid.model.RequestDocument;
import kikutaro.heroku.sendgrid.model.SentimentResult;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import static spark.Spark.port;
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
            String sgApiKey = System.getenv("SG_API_KEY");
            if(Strings.isNullOrEmpty(sgApiKey)) {
                System.out.println("SendGridのAPI KEYが登録されていません");
            }
            String sgFrom = System.getenv("SG_FROM");
            if(Strings.isNullOrEmpty(sgApiKey)) {
                System.out.println("Fromが登録されていません");
            }
            
            Gson gson = new Gson();
            
            System.out.println("リクエストのパース開始");
            String from = null;
            String text = null;
            PlotlyResult retPlot;
            List<FileItem> items = upload.parseRequest(req.raw());
            for(FileItem fi : items) {
                if(fi.isFormField()) {
                    System.out.println(fi.getFieldName());
                    System.out.println(fi.getString());
                    
                    if(StringUtils.equals(fi.getFieldName(), "from")) {
                        from = fi.getString();
                    } else if(StringUtils.equals(fi.getFieldName(), "text")) {
                        text = fi.getString();
                    } else if(!Strings.isNullOrEmpty(from) && !Strings.isNullOrEmpty(text)) {
                        SentimentRequest csObj = new SentimentRequest();
                        RequestDocument doc = new RequestDocument();
                        doc.setId(Long.toString(System.currentTimeMillis()));
                        doc.setLanguage("en");
                        doc.setText(text);
                        csObj.setDocuments(Arrays.asList(doc));
                        
                        try {
                            HttpResponse<JsonNode> sentimentRet = Unirest.post("https://westus.api.cognitive.microsoft.com/text/analytics/v2.0/sentiment")
                                    .header("Content-Type", "application/json")
                                    .header("Ocp-Apim-Subscription-Key", cognitiveKey)
                                    .body(gson.toJson(csObj))
                                    .asJson();
                            System.out.println("JSONデータ : " + gson.toJson(csObj));
                            System.out.println("あなたの送信したメールのSentimet : " + sentimentRet.getBody().toString());
                            
                            SentimentResult sentimentResult = gson.fromJson(sentimentRet.getBody().toString(), SentimentResult.class);
                            System.out.println(sentimentResult.getDocuments().get(0).getScore());
                            
                            HttpResponse<JsonNode> plotlyGridRet = Unirest.post("https://api.plot.ly/v2/grids")
                                    .basicAuth(plotlyUser, plotlyPass)
                                    .header("Content-Type", "application/json")
                                    .header("Plotly-Client-Platform", "Java")
                                    .body(PlotlyHelper.gridSentimentData(sentimentResult.getDocuments().get(0).getScore()))
                                    .asJson();
                            
                            PlotlyResult retGrid = gson.fromJson(plotlyGridRet.getBody().toString(), PlotlyResult.class);
                            
                            HttpResponse<JsonNode> plotlyPlotRet = Unirest.post("https://api.plot.ly/v2/plots")
                                    .basicAuth(plotlyUser, plotlyPass)
                                    .header("Content-Type", "application/json")
                                    .header("Plotly-Client-Platform", "Java")
                                    .body(PlotlyHelper.plotPieData(retGrid.getFile().getFid(), retGrid.getFile().getCols().get(0).getUid(), retGrid.getFile().getCols().get(1).getUid()))
                                    .asJson();
                            retPlot = gson.fromJson(plotlyPlotRet.getBody().toString(), PlotlyResult.class);
                            
                            System.err.println(retPlot.getFile().getEmbed_url());
                            System.err.println(retPlot.getFile().getWeb_url());
                            
                            if(!Strings.isNullOrEmpty(from)) {
                                System.out.println("prepare sending return mail.");
                                Email to = new Email(from);
                                Content content = new Content("text/html", 
                                        "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
                                                + "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head></head><body>"
                                                + "<div><a href=\"" + retPlot.getFile().getWeb_url() + "\" target=\"_blank\" style=\"display: block; text-align: center;\"><img src=\"" + retPlot.getFile().getWeb_url() + ".png\" alt=\"\" style=\"max-width: 100%;width: 600px;\"  width=\"600\" /></a><script data-plotly=\"" + retPlot.getFile().getFid() + "\" src=\"https://plot.ly/embed.js\" async></script></div>"
                                                + "<p>Checked sentence</p>"
                                                + text
                                                + "</body></html>");
                                Mail mail = new Mail(new Email(sgFrom), "Result setiment of previous your mail.", to, content);
                                SendGrid sg = new SendGrid(sgApiKey);
                                Request request = new Request();
                                request.method = Method.POST;
                                request.endpoint = "mail/send";
                                try {
                                    request.body = mail.build();
                                } catch (IOException ex) {
                                    System.out.println(ex.getMessage());
                                }
                                try {
                                    Response response = sg.api(request);
                                    System.out.println(response.toString());
                                } catch (IOException ex) {
                                    System.out.println(ex.getMessage());
                                }
                            } else {
                                System.out.println("not get from address");
                            }
                        } catch (UnirestException ex) {
                            System.out.println(ex.getMessage());
                        } catch (IOException ex) {
                        }
                        break;
                    }
                }
            }
            
            return 200;
        });
    }
    
}
