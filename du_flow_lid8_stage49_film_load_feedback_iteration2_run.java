import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage49_film_load_feedback_iteration2_run {
  private static String lastSolution(Model model) {
    String[] tags = model.sol().tags();
    return tags[tags.length - 1];
  }
  private static void range(String label, double[][] values, String unit) {
    double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
    for (double v : values[0]) if (Double.isFinite(v)) { min = Math.min(min, v); max = Math.max(max, v); }
    System.out.printf(java.util.Locale.US, "%s min=%.12g[%s] max=%.12g[%s]%n", label, min, unit, max, unit);
  }
  private static void global(Model model, String tag, String expr, String unit) {
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", "dset_tff_gap_feedback49");
    model.result().numerical(tag).set("expr", new String[]{expr});
    model.result().numerical(tag).set("unit", new String[]{unit});
    model.result().numerical(tag).setResult();
    range(tag, model.result().numerical(tag).getReal(), unit);
  }
  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\127_lid8mm_stage49_h3um_film_load_feedback_iteration2_setup.mph");
    model.label("128_lid8mm_stage49_h3um_film_load_feedback_iteration2_results.mph");
    model.study("std_tff_gap_qs45").createAutoSequences("sol");
    String solver = lastSolution(model);
    model.sol(solver).runAll();
    model.result().dataset().create("dset_tff_gap_feedback49", "Solution");
    model.result().dataset("dset_tff_gap_feedback49").set("solution", solver);
    global(model, "eval49_Wfilm", "W_film", "N");
    global(model, "eval49_Ffilm_shear", "F_film_shear", "N");
    global(model, "eval49_mu_film", "F_film_shear/F_total_target", "1");
    global(model, "eval49_hsep", "h_feedback_sep49", "um");
    global(model, "eval49_Wcontact_budget", "max(F_total_target-W_film,0)", "N");
    model.save(
        "D:\\COMSOL_Outputs\\models\\du\\flow\\128_lid8mm_stage49_h3um_film_load_feedback_iteration2_results.mph");
    System.out.println("SAVED_STAGE49_RESULTS=128_lid8mm_stage49_h3um_film_load_feedback_iteration2_results.mph");
  }
}
