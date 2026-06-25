import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_mixed_lubrication_stage11_weakcoupled_film_h12_run {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\34_lid8mm_mixed_lubrication_stage3_moving_footprint_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\49_lid8mm_mixed_lubrication_stage11_weakcoupled_film_h12um_results.mph";

  private static String lastSolution(Model model) {
    String[] tags = model.sol().tags();
    return tags[tags.length - 1];
  }

  private static void global(Model model, String tag, String expr, String unit) {
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", "dset_tff_oneway");
    model.result().numerical(tag).set("expr", new String[]{expr});
    model.result().numerical(tag).set("unit", new String[]{unit});
    model.result().numerical(tag).setResult();
  }

  private static void maxSurface(Model model, String tag, String expr, String unit) {
    model.result().numerical().create(tag, "MaxSurface");
    model.result().numerical(tag).selection().named("sel_cornea_anterior_surface");
    model.result().numerical(tag).set("data", "dset_tff_oneway");
    model.result().numerical(tag).set("expr", new String[]{expr});
    model.result().numerical(tag).set("unit", new String[]{unit});
    model.result().numerical(tag).setResult();
  }

  private static void printRange(Model model, String tag, String unit) {
    double[][] values = model.result().numerical(tag).getReal();
    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;
    for (double v : values[0]) {
      min = Math.min(min, v);
      max = Math.max(max, v);
    }
    System.out.printf("%s min=%.12g[%s] max=%.12g[%s] final=%.12g[%s]%n",
        tag, min, unit, max, unit, values[0][values[0].length - 1], unit);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model model = ModelUtil.load("Model", IN);
    model.label("49_lid8mm_mixed_lubrication_stage11_weakcoupled_film_h12um_results.mph");
    model.param().set("h0_tear", "12[um]", "Weak-coupled mixed-lubrication film thickness baseline");
    model.study("std_tff_oneway").createAutoSequences("sol");
    String sol = lastSolution(model);
    model.sol(sol).runAll();
    model.result().dataset().create("dset_tff_oneway", "Solution");
    model.result().dataset("dset_tff_oneway").set("solution", sol);
    global(model, "eval_W_film", "W_film", "N");
    global(model, "eval_F_film_shear", "F_film_shear", "N");
    global(model, "eval_mu_app_film_only", "mu_app_film_only", "1");
    maxSurface(model, "max_pfilm", "pfilm", "Pa");
    maxSurface(model, "max_tau_film_wall", "tau_film_wall", "Pa");
    printRange(model, "eval_W_film", "N");
    printRange(model, "eval_F_film_shear", "N");
    printRange(model, "eval_mu_app_film_only", "1");
    printRange(model, "max_pfilm", "Pa");
    printRange(model, "max_tau_film_wall", "Pa");
    model.save(OUT);
    System.out.println("SAVED_STAGE5_FILM_RESULT=" + OUT);
  }
}
