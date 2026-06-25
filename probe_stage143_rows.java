import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage143_rows {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model m = ModelUtil.load("Model",
        "265_lid8mm_stage149_moving_analytic_film_10pct_results_Model.mph");
    double[][] a = m.result().numerical("eval142").getReal();
    String[] n = {"phi", "treplay", "Fcontact", "Ffilm", "Ftotal", "err", "indent"};
    for (int j = 0; j < a[0].length; j++) {
      StringBuilder b = new StringBuilder("row=" + j);
      for (int i = 0; i < a.length; i++)
        b.append(" ").append(n[i]).append("=")
            .append(String.format(Locale.US, "%.9g", a[i][j]));
      System.out.println(b);
    }
    ModelUtil.disconnect();
  }
}
