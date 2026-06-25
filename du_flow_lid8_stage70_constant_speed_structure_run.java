import com.comsol.model.*;
import com.comsol.model.util.*;
import java.util.*;

public class du_flow_lid8_stage70_constant_speed_structure_run {
  private static void global(Model m, String tag, String expr, String unit) {
    m.result().numerical().create(tag, "EvalGlobal");
    m.result().numerical(tag).set("data", "dset_constant_speed_structure70");
    m.result().numerical(tag).set("expr", new String[] {expr});
    m.result().numerical(tag).set("unit", new String[] {unit});
    m.result().numerical(tag).setResult();
    double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
    for (double v : m.result().numerical(tag).getReal()[0]) {
      if (Double.isFinite(v)) { min = Math.min(min, v); max = Math.max(max, v); }
    }
    System.out.printf(Locale.US, "%s min=%.12g[%s] max=%.12g[%s]%n", tag, min, unit, max, unit);
  }

  public static void main(String[] args) throws Exception {
    ModelUtil.initStandalone(true);
    Model m = ModelUtil.load("Model",
        "D:\\COMSOL_Outputs\\models\\du\\flow\\151_lid8mm_stage69_constant_speed_smooth_motion_setup.mph");
    m.label("152_lid8mm_stage70_constant_speed_structure_results.mph");
    System.out.println("RUN_STAGE70=std_partitioned_local_pfilm full local pfilm feedback constant-speed motion");
    m.study("std_partitioned_local_pfilm").run();
    m.result().dataset().create("dset_constant_speed_structure70", "Solution");
    m.result().dataset("dset_constant_speed_structure70").set("solution", "sol20");
    global(m, "eval70_dr", "dr_force_mixed54", "mm");
    global(m, "eval70_Wfilm_replay", "W_film_replay53", "N");
    m.save("D:\\COMSOL_Outputs\\models\\du\\flow\\152_lid8mm_stage70_constant_speed_structure_results.mph");
    System.out.println("SAVED_STAGE70=152_lid8mm_stage70_constant_speed_structure_results.mph");
  }
}
