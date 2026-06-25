import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class probe_stage555_state {
  private static void probe(String file, String solution, String dataset)
      throws Exception {
    Model model = ModelUtil.load("Model", file);
    try { model.result().dataset().remove(dataset); } catch (Exception ignored) {}
    model.result().dataset().create(dataset, "Solution");
    model.result().dataset(dataset).set("solution", solution);
    String eval = dataset + "_eval";
    model.result().numerical().create(eval, "EvalGlobal");
    model.result().numerical(eval).set("data", dataset);
    String[] expr = {
      "Wfilm555", "Fn_contact119", "Ftotal555",
      "(Ftotal555-F_total_target)/F_total_target",
      "dr_indent119", "q_force_total111",
      "intop_film(h_geom555)/intop_film(1)",
      "alpha_gap555", "h_gap_smooth555"
    };
    model.result().numerical(eval).set("expr", expr);
    double[][] values = model.result().numerical(eval).getReal();
    System.out.println("FILE=" + file + " SOL=" + solution);
    for (int i = 0; i < expr.length; i++) {
      System.out.print(expr[i] + ":");
      for (int j = 0; j < values[i].length; j++) {
        System.out.printf(Locale.US, " %.10g", values[i][j]);
      }
      System.out.println();
    }
    ModelUtil.remove("Model");
  }

  public static void main(String[] args) {
    try {
      ModelUtil.initStandalone(false);
      probe("557u_stage555_alpha0p825_true_load_scaled.mph",
          "sol77", "probe77");
      ModelUtil.disconnect();
    } catch (Exception error) {
      error.printStackTrace();
      System.exit(1);
    }
  }
}
