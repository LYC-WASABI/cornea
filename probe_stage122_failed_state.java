import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage122_failed_state {
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(false);
    Model m = ModelUtil.load("Model",
        "stage124_fast_corrected_control_70pct_output_Model.mph");
    String tag = "eval_failed122";
    try { m.result().numerical().remove(tag); } catch (Exception ignore) {}
    m.result().numerical().create(tag, "EvalGlobal");
    m.result().numerical(tag).set("data", "dset_shear_feedback76");
    String[] expr = {
        "t", "Fn_contact119", "Fn_film119", "Fn_total119",
        "Fn_error119", "q_force_total111", "q_force_total111t",
        "dr_indent119", "pfilm_ramp120", "pfilm_cap120",
        "scale_pfilm_effective120"
    };
    m.result().numerical(tag).set("expr", expr);
    double[][] v = m.result().numerical(tag).getReal();
    int n = v[0].length;
    System.out.println("points=" + n);
    for (int j = Math.max(0, n - 30); j < n; j++) {
      StringBuilder b = new StringBuilder();
      b.append("row=").append(j);
      for (int i = 0; i < expr.length; i++) {
        b.append(" ").append(expr[i]).append("=")
            .append(String.format(Locale.US, "%.9g", v[i][j]));
      }
      System.out.println(b);
    }
    ModelUtil.disconnect();
  }
}
