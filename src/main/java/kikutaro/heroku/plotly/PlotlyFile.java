package kikutaro.heroku.plotly;

import java.util.List;
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
public class PlotlyFile {
    private String fid;
    private String embed_url;
    private List<GridCol> cols;
}
