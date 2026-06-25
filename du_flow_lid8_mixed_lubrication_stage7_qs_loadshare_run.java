import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_mixed_lubrication_stage7_qs_loadshare_run {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\41_lid8mm_mixed_lubrication_stage7_qs_loadshare_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\42_lid8mm_mixed_lubrication_stage7_qs_loadshare_results.mph";

  private static String lastSolution(Model model) {
    String[] tags = model.sol().tags();
    return tags[tags.length - 1];
  }

  private static void global(Model model, String tag, String expr, String unit) {
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", "dset_qs_loadshare");
    model.result().numerical(tag).set("expr", new String[]{expr});
    model.result().numerical(tag).set("unit", new String[]{unit});
    model.result().numerical(tag).setResult();
  }

  private static void printRange(Model model, String tag, String unit) {
    double[][] values = model.result().numerical(tag).getReal();
    double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
    for (double v : values[0]) { min = Math.min(min, v); max = Math.max(max, v); }
    System.out.printf("%s min=%.12g[%s] max=%.12g[%s] final=%.12g[%s]%n",
        tag, min, unit, max, unit, values[0][values[0].length - 1], unit);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("42_lid8mm_mixed_lubrication_stage7_qs_loadshare_results.mph");
    model.study("std_qs_loadshare").createAutoSequences("sol");
    String sol = lastSolution(model);
    model.sol(sol).runAll();
    model.result().dataset().create("dset_qs_loadshare", "Solution");
    model.result().dataset("dset_qs_loadshare").set("solution", sol);
    global(model, "eval_qs_theta", "theta_qs", "deg");
    global(model, "eval_qs_dr", "dr_mixed_qs", "mm");
    global(model, "eval_qs_W_film", "W_film_qs", "N");
    global(model, "eval_qs_W_contact", "W_contact_qs", "N");
    global(model, "eval_qs_W_total", "W_total_qs", "N");
    global(model, "eval_qs_F_shear", "F_friction_qs", "N");
    global(model, "eval_qs_mu_app", "mu_app_qs", "1");
    printRange(model, "eval_qs_W_film", "N");
    printRange(model, "eval_qs_W_contact", "N");
    printRange(model, "eval_qs_W_total", "N");
    printRange(model, "eval_qs_F_shear", "N");
    printRange(model, "eval_qs_mu_app", "1");
    model.save(OUT);
    System.out.println("SAVED_STAGE7_QS_RESULT=" + OUT);
  }
}
