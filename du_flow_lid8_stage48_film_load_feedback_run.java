import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage48_film_load_feedback_run {
  private static String lastSolution(Model model) {
    String[] tags = model.sol().tags();
    return tags[tags.length - 1];
  }

  private static void printRange(String label, double[][] values, String unit) {
    double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
    for (double v : values[0]) {
      if (Double.isFinite(v)) { min = Math.min(min, v); max = Math.max(max, v); }
    }
    System.out.printf(java.util.Locale.US, "%s min=%.12g[%s] max=%.12g[%s]%n",
        label, min, unit, max, unit);
  }

  private static void global(Model model, String tag, String expr, String unit) {
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", "dset_tff_gap_feedback48");
    model.result().numerical(tag).set("expr", new String[]{expr});
    model.result().numerical(tag).set("unit", new String[]{unit});
    model.result().numerical(tag).setResult();
    printRange(tag, model.result().numerical(tag).getReal(), unit);
  }

  private static void surface(Model model, String type, String tag, String expr, String unit) {
    model.result().numerical().create(tag, type);
    model.result().numerical(tag).set("data", "dset_tff_gap_feedback48");
    model.result().numerical(tag).selection().named("sel_cornea_anterior_surface");
    model.result().numerical(tag).set("expr", new String[]{expr});
    model.result().numerical(tag).set("unit", new String[]{unit});
    model.result().numerical(tag).setResult();
    printRange(tag, model.result().numerical(tag).getReal(), unit);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\125_lid8mm_stage48_h3um_film_load_feedback_setup.mph");
    model.label("126_lid8mm_stage48_h3um_film_load_feedback_results.mph");
    model.study("std_tff_gap_qs45").createAutoSequences("sol");
    String solver = lastSolution(model);
    System.out.println("RUN_STAGE48_FEEDBACK_SOLVER=" + solver);
    model.sol(solver).runAll();
    model.result().dataset().create("dset_tff_gap_feedback48", "Solution");
    model.result().dataset("dset_tff_gap_feedback48").set("solution", solver);
    global(model, "eval48_Wfilm", "W_film", "N");
    global(model, "eval48_Ffilm_shear", "F_film_shear", "N");
    global(model, "eval48_mu_film", "F_film_shear/F_total_target", "1");
    global(model, "eval48_hsep", "h_feedback_sep48", "um");
    global(model, "eval48_Wcontact_budget", "max(F_total_target-W_film,0)", "N");
    surface(model, "MinSurface", "min48_hfilm", "h_film_input", "um");
    surface(model, "MaxSurface", "max48_pfilm", "max(pfilm,0)", "Pa");
    model.save(
        "D:\\COMSOL_Outputs\\models\\du\\flow\\126_lid8mm_stage48_h3um_film_load_feedback_results.mph");
    System.out.println("SAVED_STAGE48_RESULTS=126_lid8mm_stage48_h3um_film_load_feedback_results.mph");
  }
}
