package kikutaro.heroku.sendgrid.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author kikuta
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ResultDocument {
    private double score;
    private String id;
}
