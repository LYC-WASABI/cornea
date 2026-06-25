import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage29_reaction_probe {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\86_lid8mm_stage29_tabulated_reaction_calibrated_short_results.mph";

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    try { model.result().numerical().remove("gev_stage29"); } catch (Exception ignored) {}
    model.result().numerical().create("gev_stage29", "EvalGlobal");
    model.result().numerical("gev_stage29").set("data", "dset5");
    model.result().numerical("gev_stage29").set("expr",
        new String[]{"comp1.W_reaction_current29", "comp1.W_film_replay",
            "comp1.W_reaction_current29", "comp1.W_reaction_current29-F_total_target",
            "comp1.dr_force_reaction29"});
    double[][] values = model.result().numerical("gev_stage29").getReal();
    for (int i = 0; i < values.length; i++) {
      double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
      for (double x : values[i]) { min = Math.min(min, x); max = Math.max(max, x); }
      System.out.println("EXPR_" + i + "_MIN=" + min + " MAX=" + max);
    }
  }
}
