package kikutaro.heroku.sendgrid.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Cognitive Service Documents.
 * 
 * @author kikuta
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class RequestDocument {
    private String language;
    private String id;
    private String text;
}
