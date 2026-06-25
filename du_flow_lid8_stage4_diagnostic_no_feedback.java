import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_stage4_diagnostic_no_feedback {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\36_lid8mm_mixed_lubrication_stage4_bidirectional_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\37_lid8mm_mixed_lubrication_stage4_bidirectional_short_results.mph";

  private static String lastSolution(Model model) {
    String[] tags = model.sol().tags();
    return tags[tags.length - 1];
  }

  private static void global(Model model, String tag, String expr, String unit) {
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", "dset_mixed_short");
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
    model.label("37_lid8mm_mixed_lubrication_stage4_bidirectional_short_results.mph");
    model.component("comp1").physics("solid").feature("load_tearfilm_base").active(false);
    model.study("std_mixed_coupled").feature("time").set("tlist", "range(0,0.001,0.02)");
    model.study("std_mixed_coupled").createAutoSequences("sol");
    String sol = lastSolution(model);
    model.sol(sol).feature("t1").set("consistent", "off");
    model.sol(sol).feature("t1").feature().remove("se1");
    model.sol(sol).feature("t1").create("fc1", "FullyCoupled");
    model.sol(sol).feature("t1").feature("fc1").set("linsolver", "d1");
    model.sol(sol).runAll();
    model.result().dataset().create("dset_mixed_short", "Solution");
    model.result().dataset("dset_mixed_short").set("solution", sol);
    global(model, "eval_W_film_short", "W_film", "N");
    global(model, "eval_W_contact_short", "W_contact", "N");
    global(model, "eval_W_total_short", "W_total_mixed", "N");
    global(model, "eval_mu_app_short", "mu_app_mixed", "1");
    printRange(model, "eval_W_film_short", "N");
    printRange(model, "eval_W_contact_short", "N");
    printRange(model, "eval_W_total_short", "N");
    printRange(model, "eval_mu_app_short", "1");
    model.save(OUT);
    System.out.println("SAVED_STAGE4_SHORT_RESULT=" + OUT);
  }
}
