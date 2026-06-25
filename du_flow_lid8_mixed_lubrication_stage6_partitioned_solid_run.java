import com.comsol.model.*;
import com.comsol.model.util.*;

public class du_flow_lid8_mixed_lubrication_stage6_partitioned_solid_run {
  private static final String IN =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\39_lid8mm_mixed_lubrication_stage6_partitioned_solid_setup.mph";
  private static final String OUT =
      "D:\\COMSOL_Outputs\\models\\du\\flow\\40_lid8mm_mixed_lubrication_stage6_partitioned_solid_results.mph";

  private static String lastSolution(Model model) {
    String[] tags = model.sol().tags();
    return tags[tags.length - 1];
  }

  private static void global(Model model, String tag, String expr, String unit) {
    model.result().numerical().create(tag, "EvalGlobal");
    model.result().numerical(tag).set("data", "dset_partitioned_solid");
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
    model.label("40_lid8mm_mixed_lubrication_stage6_partitioned_solid_results.mph");
    model.study("std_partitioned_solid").createAutoSequences("sol");
    String sol = lastSolution(model);
    model.sol(sol).runAll();
    model.result().dataset().create("dset_partitioned_solid", "Solution");
    model.result().dataset("dset_partitioned_solid").set("solution", sol);
    global(model, "eval_part_W_film", "W_film_replay", "N");
    global(model, "eval_part_W_contact", "W_contact", "N");
    global(model, "eval_part_W_total", "W_total_partitioned", "N");
    global(model, "eval_part_F_shear", "F_friction_partitioned", "N");
    global(model, "eval_part_mu_app", "mu_app_partitioned", "1");
    printRange(model, "eval_part_W_film", "N");
    printRange(model, "eval_part_W_contact", "N");
    printRange(model, "eval_part_W_total", "N");
    printRange(model, "eval_part_F_shear", "N");
    printRange(model, "eval_part_mu_app", "1");
    model.save(OUT);
    System.out.println("SAVED_STAGE6_RESULT=" + OUT);
  }
}
