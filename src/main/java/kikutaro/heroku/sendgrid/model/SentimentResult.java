package kikutaro.heroku.sendgrid.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author kikuta
 */
@Getter @Setter
public class SentimentResult {
    private List<ResultDocument> documents;
    
    public SentimentResult() {
        documents = new ArrayList<>();
    }
}
