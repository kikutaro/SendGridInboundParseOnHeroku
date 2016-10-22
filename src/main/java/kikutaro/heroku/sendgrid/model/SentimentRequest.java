package kikutaro.heroku.sendgrid.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Cognitive Service Schema Type.
 * 
 * @author kikuta
 */
@Getter @Setter
public class SentimentRequest {
    private List<RequestDocument> documents;
    
    public SentimentRequest() {
        documents = new ArrayList<>();
    }
}
