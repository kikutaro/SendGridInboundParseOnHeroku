package kikutaro.heroku.plotly;

import com.google.gson.stream.JsonWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Helper class of Plotly API and JSON.
 * 
 * https://plot.ly/
 * 
 * @author kikuta
 */
public class PlotlyHelper {
        
    public static String gridSentimentData(double sentiment) throws IOException {
        StringWriter sw = new StringWriter();
        try (JsonWriter writer = new JsonWriter(new BufferedWriter(sw))) {
            writer.beginObject();
            writer.name("data");
            writer.beginObject().name("cols");
            writer.beginObject().name("first column");
            writer.beginObject().name("data")
                    .beginArray()
                    .value("positive").value("negative")
                    .endArray();
            writer.name("order").value(0).endObject();
            writer.name("second column");
            writer.beginObject().name("data")
                    .beginArray()
                    .value(sentiment * 100).value((1 - sentiment) * 100)
                    .endArray();
            writer.name("order").value(1).endObject();
            writer.endObject();
            writer.endObject();
            writer.endObject();
        }
        return sw.getBuffer().toString();
    }
    
    public static String plotPieData(String uid, String textUid, String valueUid) throws IOException {
        StringWriter sw = new StringWriter();
        try (JsonWriter writer = new JsonWriter(new BufferedWriter(sw))) {
            writer.beginObject();
            writer.name("figure");
            writer.beginObject().name("data")
                    .beginArray()
                    .beginObject()
                    .name("textsrc").value(uid + ":" + textUid)
                    .name("valuessrc").value(uid + ":" + valueUid)
                    .name("type").value("pie")
                    .endObject()
                    .endArray()
                    .name("layout").beginObject().name("title").value("Your Email Sentiment").endObject()
                    .endObject()
                    .name("world_readable").value("true")
                    .endObject();
        }
        return sw.getBuffer().toString();
    }
}
