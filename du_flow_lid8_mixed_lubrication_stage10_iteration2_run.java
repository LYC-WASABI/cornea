import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_mixed_lubrication_stage10_iteration2_run {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\47_lid8mm_mixed_lubrication_stage10_calibration_iteration2_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\48_lid8mm_mixed_lubrication_stage10_calibration_iteration2_results.mph";

  private static String lastSolution(Model model) {
    String[] tags = model.sol().tags();
    return tags[tags.length - 1];
  }

  private static void global(Model model, String tag, String expr, String unit) {
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", "dset_structure_pseudotime");
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
    model.label("48_lid8mm_mixed_lubrication_stage10_calibration_iteration2_results.mph");
    model.study("std_structure_pseudotime").createAutoSequences("sol");
    String sol = lastSolution(model);
    model.sol(sol).runAll();
    model.result().dataset().create("dset_structure_pseudotime", "Solution");
    model.result().dataset("dset_structure_pseudotime").set("solution", sol);
    global(model, "eval_struct_fraction", "slide_fraction_structure", "1");
    global(model, "eval_struct_dr", "dr_mixed_structure", "mm");
    global(model, "eval_struct_W_film", "W_film_structure", "N");
    global(model, "eval_struct_W_contact", "W_contact_structure", "N");
    global(model, "eval_struct_W_total", "W_total_structure", "N");
    global(model, "eval_struct_F_shear", "F_friction_structure", "N");
    global(model, "eval_struct_mu_app", "mu_app_structure", "1");
    printRange(model, "eval_struct_W_film", "N");
    printRange(model, "eval_struct_W_contact", "N");
    printRange(model, "eval_struct_W_total", "N");
    printRange(model, "eval_struct_F_shear", "N");
    printRange(model, "eval_struct_mu_app", "1");
    model.save(OUT);
    System.out.println("SAVED_STAGE8_RESULT=" + OUT);
  }
}
